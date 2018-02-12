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

/**
 * Try block that throws specific exception.
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @param <E> Exception
 * @since 1.0
 */
public interface MappedTryBlock<E extends Exception> {

    /**
     * Execute scalar through exception handling.
     * @param scalar Scalar
     * @param <T> Scalar type
     * @return Scalar value
     * @throws Exception Exception
     */
    <T> T exec(ThrowableScalar<T, Exception> scalar) throws E;

    /**
     * Execute void procedure through exception handling.
     * @param proc Proc
     * @throws Exception Exception
     */
    void exec(ThrowableVoid<Exception> proc) throws E;
}
