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
import org.cactoos.Proc;
import org.cactoos.Scalar;

/**
 * TryWith.
 * @author Vedran Grgo Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class TryWith implements Checkable {

    /**
     * List of classes.
     */
    private final Array<Class<?>> classes;

    /**
     * List of process that handle an exception.
     */
    private final Proc<Exception> process;

    /**
     * Ctor.
     * @param cls Class
     * @param proc Proc
     * @param <T> Type of exception
     */
    @SuppressWarnings("unchecked")
    public <T extends Exception> TryWith(final Class<T> cls,
        final Proc<T> proc) {
        this(new Array<>(cls), (Proc<Exception>) proc);
    }

    /**
     * Ctor.
     * @param classes List of classes
     * @param consumer Process that handles an exception
     */
    public TryWith(final Array<Class<?>> classes,
        final Proc<Exception> consumer) {
        this.classes = new Array<>(classes);
        this.process = consumer;
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
        if (this.supports(exception)) {
            this.process.exec(exception);
        }
    }

    /**
     * Checks if exception control instance handles given exception.
     * @param exception Exception
     * @return Boolean
     */
    private boolean supports(final Exception exception) {
        boolean supports = false;
        for (final Class<?> cls : this.classes) {
            if (cls.equals(exception.getClass())) {
                supports = true;
                break;
            }
        }
        return supports;
    }
}
