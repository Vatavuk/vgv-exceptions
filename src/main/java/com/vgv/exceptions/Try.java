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
import org.cactoos.Scalar;

/**
 * Try.
 * @author Vedran Grgo Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class Try implements Checkable {

    /**
     * List of exception control objects.
     */
    private final Array<Checkable> checkables;

    /**
     * Ctor.
     * @param checkable Exception control objects
     */
    public Try(final Checkable... checkable) {
        this(new Array<>(checkable));
    }

    /**
     * Ctor.
     * @param chbls Exception control objects
     */
    public Try(final Array<Checkable> chbls) {
        this.checkables = chbls;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public <T> T valueOf(final Scalar<T> scalar) throws Exception {
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

    @Override
    public void handle(final Exception exception) throws Exception {
        for (final Checkable checkable : this.checkables) {
            checkable.handle(exception);
        }
    }

    /**
     * Finally.
     * @param proc Proc
     * @return ExceptionControl ExceptionControl
     */
    public Checkable withFinally(final VoidProc proc) {
        return new Try.Finally(this, proc);
    }

    /**
     * Finally.
     */
    private static class Finally implements Checkable {

        /**
         * Exception control origin.
         */
        private final Checkable origin;

        /**
         * Proc.
         */
        private final VoidProc fproc;

        /**
         * Ctor.
         * @param control Exception control
         * @param proc Proc
         */
        Finally(final Checkable control, final VoidProc proc) {
            this.origin = control;
            this.fproc = proc;
        }

        @Override
        public <T> T valueOf(final Scalar<T> scalar) throws Exception {
            try {
                return this.origin.valueOf(scalar);
            } finally {
                this.fproc.exec();
            }
        }

        @Override
        public void exec(final VoidProc proc) throws Exception {
            try {
                this.origin.exec(proc);
            } finally {
                this.fproc.exec();
            }
        }

        @Override
        public void handle(final Exception exception) throws Exception {
            this.origin.handle(exception);
        }
    }
}
