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

import java.util.Comparator;
import java.util.function.Consumer;
import org.cactoos.list.ListOf;

/**
 * Exception handling in catch block.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Vedran Grgo Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class Catch implements CatchBlock {

    /**
     * List of exception classes.
     */
    private final Iterable<Class<?>> classes;

    /**
     * Consumer that handles exception.
     */
    private final Consumer<Exception> consumer;

    /**
     * Ctor.
     * @param cls Class
     * @param csm Consumer that handles an exception
     * @param <T> Extends Exception
     */
    @SuppressWarnings("unchecked")
    public <T extends Exception> Catch(final Class<T> cls,
        final Consumer<T> csm) {
        this(new ListOf<>(cls), (Consumer<Exception>) csm);
    }

    /**
     * Ctor.
     * @param clazzs List of classes
     * @param csm Consumer that handles an exception
     */
    public Catch(final Iterable<Class<?>> clazzs,
        final Consumer<Exception> csm) {
        this.classes = clazzs;
        this.consumer = csm;
    }

    @Override
    public void handle(final Exception exception) {
        if (this.supports(exception)) {
            this.consumer.accept(exception);
        }
    }

    @Override
    public boolean supports(final Exception exception) {
        return new CompareClasses.DistanceMatch(
            this.distance(exception)
        ).value();
    }

    @Override
    public int distance(final Exception exception) {
        return new ListOf<>(this.classes).stream()
            .map(
                cls -> new CompareClasses(
                    exception.getClass(), cls
                ).value()
            )
            .min(Comparator.comparing(dist -> dist))
            .orElse(new CompareClasses.NoMatch().value());
    }
}
