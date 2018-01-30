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
 * Exception control decorator that does not throw checked {@link Exception}.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Vedran Grgo Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 0.28.2
 */
public final class UncheckedTry implements Checkable<Exception> {

    /**
     * Checkable origin.
     */
    private final Checkable<? extends Exception> origin;

    /**
     * Ctor.
     * @param checkable Checkable
     */
    public UncheckedTry(final Checkable<? extends Exception> checkable) {
        this.origin = checkable;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public <T> T exec(final Scalar<T> scalar) {
        try {
            return this.origin.exec(scalar);
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
}
