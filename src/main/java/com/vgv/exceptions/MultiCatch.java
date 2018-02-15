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
import org.cactoos.list.ListOf;

/**
 * Multiple catch blocks.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class MultiCatch implements CatchBlocks {

    /**
     * List of catch blocks.
     */
    private final Iterable<CatchBlock> blocks;

    /**
     * Ctor.
     * @param blks Catch block list
     */
    public MultiCatch(final CatchBlock... blks) {
        this(new ListOf<>(blks));
    }

    /**
     * Ctor.
     * @param blks Catch block list
     */
    public MultiCatch(final Iterable<CatchBlock> blks) {
        this.blocks = blks;
    }

    @Override
    public void handle(final Exception exception) {
        new ListOf<>(this.blocks).stream()
            .min(
                Comparator.comparing(
                    block -> block.distance(exception)
                )
            )
            .ifPresent(block -> block.handle(exception));
    }

    @Override
    public boolean supports(final Exception exception) {
        return new ListOf<>(this.blocks)
            .stream().anyMatch(block -> block.supports(exception));
    }
}
