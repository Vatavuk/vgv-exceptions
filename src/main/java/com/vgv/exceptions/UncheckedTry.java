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

import java.io.IOException;
import java.io.UncheckedIOException;
import org.cactoos.Scalar;

/**
 * UncheckedTry.
 * @author Vedran Grgo Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class UncheckedTry implements UncheckedCheckable {

    /**
     * Exception control origin.
     */
    private final Checkable origin;

    /**
     * Ctor.
     * @param control Exception control
     */
    public UncheckedTry(final Checkable control) {
        this.origin = control;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public <T> T valueOf(final Scalar<T> scalar) {
        try {
            return this.origin.valueOf(scalar);
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exp) {
            throw new UncheckedIOException(new IOException(exp));
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void exec(final VoidProc proc) {
        try {
            this.origin.exec(proc);
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exp) {
            throw new UncheckedIOException(new IOException(exp));
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void handle(final Exception exception) {
        try {
            this.origin.handle(exception);
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exp) {
            throw new UncheckedIOException(new IOException(exp));
        }
    }

    /**
     * Finally.
     * @param proc Proc
     * @return UncheckedExceptionControl UncheckedExceptionControl
     */
    public UncheckedCheckable withFinally(final UncheckedVoidProc proc) {
        return new UncheckedTry.Finally(this, proc);
    }

    /**
     * Finally.
     */
    private static class Finally implements UncheckedCheckable {

        /**
         * Unchecked exception control origin.
         */
        private final UncheckedCheckable origin;

        /**
         * Proc.
         */
        private final UncheckedVoidProc fproc;

        /**
         * Ctor.
         * @param control Exception control
         * @param proc Proc
         */
        Finally(final UncheckedCheckable control,
            final UncheckedVoidProc proc) {
            this.origin = control;
            this.fproc = proc;
        }

        @Override
        public <T> T valueOf(final Scalar<T> scalar) {
            try {
                return this.origin.valueOf(scalar);
            } finally {
                this.fproc.exec();
            }
        }

        @Override
        public void exec(final VoidProc proc) {
            try {
                this.origin.exec(proc);
            } finally {
                this.fproc.exec();
            }
        }

        @Override
        public void handle(final Exception exception) {
            this.origin.handle(exception);
        }
    }
}
