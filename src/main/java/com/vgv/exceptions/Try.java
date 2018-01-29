/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Vedran Grgo Vatavuk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.vgv.exceptions;

import com.jcabi.immutable.Array;
import java.util.function.Function;
import org.cactoos.Scalar;

/**
 * Exception control that corresponds to java try/catch/finally statements.
 *
 * <p>If you don't want to have any checked exceptions being thrown
 * out of your {@link Try}, you can use
 * {@link com.vgv.exceptions.UncheckedTry} decorator.
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
 *             new Array<>(IllegalStateException.class, ValidationException.class),
 *             e -> LOGGER.error("Validation exception", e)
 *         )
 *      ).with(
 *            new Finally(() -> LOGGER.info("function executed")),
 *            new Throws<>(IOException::new)
 *      ).valueOf(() -> doSomething());
 * </pre>
 *
 * @author Vedran Grgo Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 0.28.2
 */
public final class Try implements Checkable<Exception> {

    /**
     * List of consumers that handle exceptions.
     */
    private final Array<Catchable> catchables;

    /**
     * Ctor.
     * @param chbls Consumers.
     */
    public Try(final Catchable... chbls) {
        this(new Array<>(chbls));
    }

    /**
     * Ctor.
     * @param chbls Consumers.
     */
    public Try(final Array<Catchable> chbls) {
        this.catchables = chbls;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public <T> T exec(final Scalar<T> scalar) throws Exception {
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
    public void exec(final VoidProc proc) throws Exception {
        try {
            proc.exec();
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exception) {
            this.handle(exception);
            throw exception;
        }
    }

    /**
     * Creates new exception control object with additional handling of finally
     * statement.
     * @param fnly Finally proc
     * @return Checkable<Exception> Exception control
     */
    public Checkable<Exception> with(VoidProc fnly) {
        return new Try.WithFinally<>(this, fnly);
    }

    /**
     * Creates new exception control object that throws specified user exception.
     * @param thrws Throws function.
     * @param <T> Extends Exception
     * @return Checkable<T> Exception control
     */
    public <T extends Exception> Checkable<T> with(
        Function<Exception, T> thrws) {
        return new Try.WithThrows<>(thrws, this.catchables);
    }

    /**
     * Creates new exception control object with additional finally/throws
     * functionality.
     * @param fnly Finally
     * @param thrws Throws
     * @param <T> Extends Exception
     * @return Checkable<T> Exception control
     */
    public <T extends Exception> Checkable<T> with(final VoidProc fnly,
        final Function<Exception, T> thrws) {
        return new Try.WithFinally<>(
            new Try.WithThrows<>(thrws, this.catchables),
            fnly
        );
    }

    /**
     * Handles exception
     * @param exception Exception
     */
    private void handle(final Exception exception) {
        for (final Catchable catchable : this.catchables) {
            catchable.handle(exception);
        }
    }

    /**
     * Exception control decorator that throws specific Exception.
     * @param <E> Extends Exception
     */
    private static class WithThrows<E extends Exception>
        implements Checkable<E> {

        /**
         * Function that wraps generic exception to a specific one.
         */
        private final Function<Exception, E> fun;

        /**
         * Consumer that handles exception.
         */
        private final Array<Catchable> catchables;

        /**
         * Ctor.
         * @param fun Function
         * @param chbls Consumer
         */
        WithThrows(final Function<Exception, E> fun,
            final Iterable<Catchable> chbls) {
            this.fun = fun;
            this.catchables = new Array<>(chbls);
        }

        @Override
        public <T> T exec(final Scalar<T> scalar) throws E {
            try {
                return scalar.value();
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final RuntimeException exception) {
                this.handleUncheckedExp(exception);
                throw exception;
            } catch (final Exception exception) {
                return this.handleCheckedExp(exception);
            }
        }

        @Override
        public void exec(final VoidProc proc) throws E {
            try {
                proc.exec();
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final RuntimeException exception) {
                this.handleUncheckedExp(exception);
                throw exception;
            } catch (final Exception exception) {
                this.handleCheckedExp(exception);
            }
        }

        private <T> T handleCheckedExp(final Exception exception) throws E {
            this.catchables.forEach(catchable -> catchable.handle(exception));
            throw this.fun.apply(exception);
        }

        private void handleUncheckedExp(final RuntimeException exception)
            throws E {
            if(this.catchables.stream().anyMatch(
                catchable -> catchable.supports(exception))) {
                this.catchables.forEach(
                    catchable -> catchable.handle(exception));
                throw this.fun.apply(exception);
            }
        }

    }

    /**
     * Exception control decorator that controls finally statement block
     * functionality.
     * @param <E> Extends Exception
     */
    private static class WithFinally<E extends Exception> implements
        Checkable<E> {

        /**
         * Exception control origin.
         */
        private final Checkable<E> origin;

        /**
         * Void procedure.
         */
        private final VoidProc fproc;

        /**
         * Ctor.
         * @param control Exception control
         * @param proc Proc
         */
        WithFinally(final Checkable<E> control, final VoidProc proc) {
            this.origin = control;
            this.fproc = proc;
        }

        @Override
        public <T> T exec(final Scalar<T> scalar) throws E {
            try {
                return this.origin.exec(scalar);
            } finally {
                new UncheckedVoidProc(this.fproc).exec();
            }
        }

        @Override
        public void exec(final VoidProc proc) throws E {
            try {
                this.origin.exec(proc);
            } finally {
                new UncheckedVoidProc(this.fproc).exec();
            }
        }
    }
}
