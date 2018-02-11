package com.vgv.exceptions.poc;

import com.jcabi.immutable.Array;
import com.vgv.exceptions.Catchable;
import com.vgv.exceptions.UncheckedVoidProc;
import com.vgv.exceptions.VoidProc;
import java.util.Comparator;
import java.util.function.Function;
import org.cactoos.collection.Sorted;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class Try implements Checkable {

    /**
     * List of consumers that handle exceptions.
     */
    private final Array<Catchable> catchables;

    /**
     * Ctor.
     * @param chbls List of catchable objects.
     */
    public Try(final Catchable... chbls) {
        this(new Array<>(chbls));
    }

    /**
     * Ctor.
     * @param chbls List of catchable objects..
     */
    public Try(final Array<Catchable> chbls) {
        this.catchables = chbls;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public <T, E extends Exception> T exec(final ThrowableScalar<T, E> scalar)
        throws E {
        try {
            return scalar.value();
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exception) {
            this.handle(exception);
            throw exception;
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public <E extends Exception> void exec(final ThrowableVoidProc<E> proc)
        throws E {
        try {
            proc.exec();
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exception) {
            this.handle(exception);
            throw exception;
        }
    }

    /**
     * Creates new Checkable object that throws specified exception.
     * @param thrws Throws function.
     * @param <T> Extends Exception
     * @return Checkable Checkable
     */
    public <T extends Exception> MappedCheckable<T> with(
        final Function<Exception, T> thrws) {
        return new Try.WithThrows<>(thrws, this.catchables);
    }

    /**
     * Creates new Checkable object with additional handling of finally
     * block.
     * @param fnly Finally proc
     * @return Checkable Checkable
     */
    public Checkable with(final VoidProc fnly) {

        final Checkable origin = this;

        return new Checkable() {
            @Override
            public <T, E extends Exception> T exec(
                final ThrowableScalar<T, E> scalar) throws E {
                try {
                    return origin.exec(scalar);
                } finally {
                    new UncheckedVoidProc(fnly).exec();
                }
            }

            @Override
            public <E extends Exception> void exec(
                final ThrowableVoidProc<E> proc)
                throws E {
                try {
                    origin.exec(proc);
                } finally {
                    new UncheckedVoidProc(fnly).exec();
                }
            }
        };
    }

    /**
     * Creates new Checkable object with additional finally/throws
     * functionality.
     * @param fnly Finally
     * @param thrws Throws
     * @param <E> Extends Exception
     * @return MappedCheckable MappedCheckable
     */
    public <E extends Exception> MappedCheckable<E> with(final VoidProc fnly,
        final Function<Exception, E> thrws) {

        final MappedCheckable<E> origin = this.with(thrws);

        return new MappedCheckable<E>() {
            @Override
            public <T> T exec(final ThrowableScalar<T, Exception> scalar)
                throws E {
                try {
                    return origin.exec(scalar);
                } finally {
                    new UncheckedVoidProc(fnly).exec();
                }
            }

            @Override
            public void exec(final ThrowableVoidProc<Exception> proc) throws E {
                try {
                    origin.exec(proc);
                } finally {
                    new UncheckedVoidProc(fnly).exec();
                }
            }
        };
    }

    /**
     * Handles exception.
     * @param exception Exception
     */
    private void handle(final Exception exception) {
        if (!this.catchables.isEmpty()) {
            final Catchable catchable = new Sorted<>(
                (left, right) ->
                    Integer.compare(
                        left.supportFactor(exception),
                        right.supportFactor(exception)),
                this.catchables
            ).iterator().next();
            catchable.handle(exception);
        }
    }

    private static final class WithThrows<E extends Exception>
        implements MappedCheckable<E> {

        /**
         * Function that wraps generic exception to a specific one.
         */
        private final Function<Exception, E> fun;

        /**
         * Consumer that handles exception.
         */
        private final Array<Catchable> catchables;

        public WithThrows(final Function<Exception, E> fun,
            final Iterable<Catchable> catchables) {
            this.fun = fun;
            this.catchables = new Array<>(catchables);
        }

        @Override
        public <T> T exec(final ThrowableScalar<T, Exception> scalar) throws E {
            try {
                return scalar.value();
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final RuntimeException exception) {
                this.handleUncheckedExp(exception);
                throw exception;
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Exception exception) {
                this.handleCheckedExp(exception);
                throw this.fun.apply(exception);
            }
        }

        @Override
        public void exec(final ThrowableVoidProc<Exception> proc) throws E {
            try {
                proc.exec();
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final RuntimeException exception) {
                this.handleUncheckedExp(exception);
                throw exception;
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Exception exception) {
                this.handleCheckedExp(exception);
                throw this.fun.apply(exception);
            }
        }

        /**
         * Handles checked exception.
         * @param exception Exception
         */
        private void handleCheckedExp(final Exception exception) {
            this.catchables.forEach(catchable -> catchable.handle(exception));
        }

        /**
         * Handles unchecked exception.
         * @param exception Exception
         * @throws E Extends Exception
         */
        private void handleUncheckedExp(final RuntimeException exception)
            throws E {
            if (!this.catchables.isEmpty()) {
                /*final Catchable catchable = new Sorted<>(
                    (left, right) ->
                        Integer.compare(
                            left.supportFactor(exception),
                            right.supportFactor(exception)),
                this.catchables
                ).iterator().next();*/
                final Catchable catchable = this.catchables.stream()
                    .max(new Comparator<Catchable>() {
                        @Override
                        public int compare(final Catchable left,
                            final Catchable right) {
                            return Integer.compare(
                                left.supportFactor(exception),
                                right.supportFactor(exception));
                        }
                    }).get();
                catchable.handle(exception);
                throw this.fun.apply(exception);
            }
        }
    }
}
