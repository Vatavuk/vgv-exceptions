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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.junit.Test;

/**
 * Test case for {@link Catch}.
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class CatchTest {

    /**
     * Catches FileNotFoundException and throws UncheckedIOException.
     */
    @Test(expected = UncheckedIOException.class)
    public void rethrowsException() {
        new Catch(
            IOException.class,
            exp -> {
                throw new UncheckedIOException(exp);
            }).handle(new FileNotFoundException("msg"));
    }

    /**
     * No exception is handled. The catch instance does not support catching
     * of IOExceptions.
     */
    @Test
    public void doesntHandleException() {
        new Catch(
            IllegalStateException.class,
            exp -> {
                throw new IllegalStateException(exp);
            }).handle(new IOException("io exception"));
    }
}
