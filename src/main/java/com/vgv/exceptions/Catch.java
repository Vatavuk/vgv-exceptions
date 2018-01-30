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
import java.util.function.Consumer;

/**
 * Catch.
 * @author Vedran Grgo Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class Catch implements Catchable {

    /**
     * List of classes.
     */
    private final Array<Class<?>> classes;

    /**
     * List of process that handle an exception.
     */
    private final Consumer<Exception> consumer;

    /**
     * Ctor.
     * @param cls Class
     * @param consumer Consumer that handles an exception
     * @param <T> Extends Exception
     */
    public <T extends Exception> Catch(final Class<T> cls,
        final Consumer<T> consumer) {
        this(new Array<>(cls), (Consumer<Exception>) consumer);
    }

    /**
     * Ctor.
     * @param classes List of classes
     * @param consumer Consumer that handles an exception
     */
    public Catch(final Array<Class<?>> classes,
        final Consumer<Exception> consumer) {
        this.classes = new Array<>(classes);
        this.consumer = consumer;
    }

    @Override
    public void handle(final Exception exception) {
        if (this.supports(exception)) {
            this.consumer.accept(exception);
        }
    }

    @Override
    public boolean supports(final Exception exception) {
        boolean supports = false;
        for (final Class<?> cls : this.classes) {
            if (cls.isInstance(exception)) {
                supports = true;
                break;
            }
        }
        return supports;
    }
}
