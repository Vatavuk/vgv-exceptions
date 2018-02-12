package com.vgv.exceptions.poc;

import com.vgv.exceptions.UncheckedFinally;
import java.util.function.Function;

/**
 * Exception control that corresponds to java try/catch/finally statements.
 *
 * <p>If you don't want to have any checked exceptions being thrown
 * out of your {@link com.vgv.exceptions.Try}, you can use
 * {@link com.vgv.exceptions.UncheckedTry} decorator.
 *
 * <p>There is no thread-safety guarantee.
 *
 * <p>This is how you're supposed to use it:
 *
 * <pre> new Try(
 *         new Catch(
 *            ServerException.class,
 *            e -> LOGGER.error("Server exception", e)
 *         ),
 *         new Catch(
 *             ClientException.class,
 *             e -> LOGGER.error("client exception", e)
 *         ),
 *         new Catch(
 *             new Array<>(IllegalStateException.class,
 *                  ValidationException.class),
 *             e -> LOGGER.error("Validation exception", e)
 *         )
 *      ).with(
 *            new Finally(() -> LOGGER.info("function executed")),
 *            new Throws<>(IOException::new)
 *      ).exec(() -> doSomething());
 * </pre>
 * @author Vedran Grgo Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class Try implements TryBlock {

    /**
     * Catch blocks.
     */
    private final CatchBlocks blocks;

    /**
     * Ctor.
     * @param blks List of catch blocks.
     */
    public Try(final CatchBlock... blks) {
        this(new MultiCatch(blks));
    }

    /**
     * Ctor.
     * @param blks Catch bloks
     */
    public Try(final CatchBlocks blks) {
        this.blocks = blks;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public <T, E extends Exception> T exec(final ThrowableScalar<T, E> scalar)
        throws E {
        try {
            return scalar.value();
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exception) {
            this.blocks.handle(exception);
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
            this.blocks.handle(exception);
            throw exception;
        }
    }

    /**
     * Creates new TryBlock object with additional handling of finally
     * block.
     * @param fnly Finally proc
     * @return Checkable Checkable
     */
    public TryBlock with(final FinallyBlock fnly) {

        final TryBlock origin = this;

        return new TryBlock() {
            @Override
            public <T, E extends Exception> T exec(
                final ThrowableScalar<T, E> scalar) throws E {
                try {
                    return origin.exec(scalar);
                } finally {
                    new UncheckedFinally(fnly).exec();
                }
            }

            @Override
            public <E extends Exception> void exec(
                final ThrowableVoidProc<E> proc)
                throws E {
                try {
                    origin.exec(proc);
                } finally {
                    new UncheckedFinally(fnly).exec();
                }
            }
        };
    }

    /**
     * Creates new TryBlock object that throws specific exception.
     * @param thrws Throws function.
     * @param <T> Extends Exception
     * @return Checkable Checkable
     */
    public <T extends Exception> MappedTryBlock<T> with(
        final Function<Exception, T> thrws) {
        return new Try.WithThrows<>(thrws, this.blocks);
    }

    /**
     * Creates new TryBlock object with additional finally/throws
     * functionality.
     * @param fnly Finally
     * @param thrws Throws
     * @param <E> Extends Exception
     * @return MappedCheckable MappedCheckable
     */
    public <E extends Exception> MappedTryBlock<E> with(final FinallyBlock fnly,
        final Function<Exception, E> thrws) {

        final MappedTryBlock<E> origin = this.with(thrws);

        return new MappedTryBlock<E>() {
            @Override
            public <T> T exec(final ThrowableScalar<T, Exception> scalar)
                throws E {
                try {
                    return origin.exec(scalar);
                } finally {
                    new UncheckedFinally(fnly).exec();
                }
            }

            @Override
            public void exec(final ThrowableVoidProc<Exception> proc) throws E {
                try {
                    origin.exec(proc);
                } finally {
                    new UncheckedFinally(fnly).exec();
                }
            }
        };
    }

    /**
     * Exception control that throws specific exception.
     * @param <E> Exception
     */
    private static final class WithThrows<E extends Exception>
        implements MappedTryBlock<E> {

        /**
         * Function that wraps exception to a specific one.
         */
        private final Function<Exception, E> fun;

        /**
         * Catch blocks.
         */
        private final CatchBlocks blocks;

        /**
         * Ctor.
         * @param func Function
         * @param blcks Catch blocks
         */
        public WithThrows(final Function<Exception, E> func,
            final Iterable<CatchBlock> blcks) {
            this(func, new MultiCatch(blcks));
        }

        /**
         * Ctor.
         * @param func Func
         * @param blcks Catch blocks
         */
        public WithThrows(final Function<Exception, E> func,
            final CatchBlocks blcks) {
            this.fun = func;
            this.blocks = blcks;
        }

        @Override
        public <T> T exec(final ThrowableScalar<T, Exception> scalar) throws E {
            try {
                return scalar.value();
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final RuntimeException exception) {
                return this.handleUnchecked(exception);
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Exception exception) {
                return this.handle(exception);
            }
        }

        @Override
        public void exec(final ThrowableVoidProc<Exception> proc) throws E {
            try {
                proc.exec();
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final RuntimeException exception) {
                this.handleUnchecked(exception);
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Exception exception) {
                this.handle(exception);
            }
        }

        private <T> T handle(final Exception exception) throws E {
            this.blocks.handle(exception);
            throw this.fun.apply(exception);
        }

        private <T> T handleUnchecked(final RuntimeException exception) throws E {
            if (this.blocks.supports(exception)) {
                this.handle(exception);
            }
            throw exception;
        }
    }
}
