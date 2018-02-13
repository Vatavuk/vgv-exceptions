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
@SuppressWarnings("PMD.TooManyMethods")
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
    public <E extends Exception> void exec(final ThrowableVoid<E> proc)
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
        return new Try.WithFinally(this, fnly);
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
        return new Try.WithThrowsFinally<>(this.with(thrws), fnly);
    }

    /**
     * TryBlock with additional handling of finally block.
     */
    private static final class WithFinally implements TryBlock {

        /**
         * TryBlock origin.
         */
        private final TryBlock origin;

        /**
         * Finally block.
         */
        private final FinallyBlock fnly;

        /**
         * Ctor.
         * @param tblk TryBlock
         * @param fblk FinallyBlock
         */
        WithFinally(final TryBlock tblk, final FinallyBlock fblk) {
            this.origin = tblk;
            this.fnly = fblk;
        }

        @Override
        public <T, E extends Exception> T exec(
            final ThrowableScalar<T, E> scalar) throws E {
            try {
                return this.origin.exec(scalar);
            } finally {
                new UncheckedFinally(this.fnly).exec();
            }
        }

        @Override
        public <E extends Exception> void exec(
            final ThrowableVoid<E> proc)
            throws E {
            try {
                this.origin.exec(proc);
            } finally {
                new UncheckedFinally(this.fnly).exec();
            }
        }
    }

    /**
     * Exception control that throws specific exception.
     * @param <E> Exception
     */
    private static final class WithThrows<E extends Exception> implements
        MappedTryBlock<E> {

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
        WithThrows(final Function<Exception, E> func,
            final Iterable<CatchBlock> blcks) {
            this(func, new MultiCatch(blcks));
        }

        /**
         * Ctor.
         * @param func Func
         * @param blcks Catch blocks
         */
        WithThrows(final Function<Exception, E> func,
            final CatchBlocks blcks) {
            this.fun = func;
            this.blocks = blcks;
        }

        @Override
        @SuppressWarnings("PMD.AvoidCatchingGenericException")
        public <T> T exec(final ThrowableScalar<T, Exception> scalar) throws E {
            try {
                return scalar.value();
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final RuntimeException exception) {
                if (this.blocks.supports(exception)) {
                    this.handle(exception);
                }
                throw exception;
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Exception exception) {
                this.blocks.handle(exception);
                throw this.fun.apply(exception);
            }
        }

        @Override
        @SuppressWarnings("PMD.AvoidCatchingGenericException")
        public void exec(final ThrowableVoid<Exception> proc) throws E {
            try {
                proc.exec();
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final RuntimeException exception) {
                if (this.blocks.supports(exception)) {
                    this.handle(exception);
                }
                throw exception;
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Exception exception) {
                this.handle(exception);
            }
        }

        /**
         * Handle exception.
         * @param exception Exception
         * @throws E Exception
         */
        private void handle(final Exception exception) throws E {
            this.blocks.handle(exception);
            throw this.fun.apply(exception);
        }
    }

    /**
     * TryBlock object with additional finally/throws functionality.
     * @param <E> Exception
     */
    private static final class WithThrowsFinally<E extends Exception> implements
        MappedTryBlock<E> {

        /**
         * Mapped Try block origin.
         */
        private final MappedTryBlock<E> origin;

        /**
         * Finally block.
         */
        private final FinallyBlock fnly;

        /**
         * Ctor.
         * @param tblock Mapepd Try block
         * @param fblock Finally block
         */
        WithThrowsFinally(final MappedTryBlock<E> tblock,
            final FinallyBlock fblock) {
            this.origin = tblock;
            this.fnly = fblock;
        }

        @Override
        public <T> T exec(final ThrowableScalar<T, Exception> scalar) throws E {
            try {
                return this.origin.exec(scalar);
            } finally {
                new UncheckedFinally(this.fnly).exec();
            }
        }

        @Override
        public void exec(final ThrowableVoid<Exception> proc) throws E {
            try {
                this.origin.exec(proc);
            } finally {
                new UncheckedFinally(this.fnly).exec();
            }
        }
    }
}
