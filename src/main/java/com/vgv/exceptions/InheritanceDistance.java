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

import org.cactoos.Scalar;

/**
 * Compares two classes and calculates inheritance distance between them.
 * 0 -> full match. (matching IOException with IOException)
 * 1 -> one inheritance level. (matching IOException with
 * FileNotFoundException)
 * 2 -> two inheritance levels. (matching Exception with
 * FileNotFoundException)
 * ...
 * 999 -> no match. (matching IOException with RuntimeException)
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class InheritanceDistance implements Scalar<Integer> {

    /**
     * Level that specifies that two classes are identical.
     */
    private static final int FULL_MATCH = 0;

    /**
     * Level that specifies that classes are not related.
     */
    private static final int NO_MATCH = 999;

    /**
     * Base class.
     */
    private final Class<?> base;

    /**
     * Class to be compared.
     */
    private final Class<?> comparing;

    /**
     * Ctor.
     * @param from From
     * @param towards To
     */
    public InheritanceDistance(final Class<?> from, final Class<?> towards) {
        this.base = from;
        this.comparing = towards;
    }

    @Override
    public Integer value() {
        int factor = InheritanceDistance.NO_MATCH;
        if (this.comparing.equals(this.base)) {
            factor = InheritanceDistance.FULL_MATCH;
        } else {
            Class<?> sclass = this.base.getSuperclass();
            int idx = 0;
            while (!sclass.equals(Object.class)) {
                idx += 1;
                if (sclass.equals(this.comparing)) {
                    factor = idx;
                    break;
                }
                sclass = sclass.getSuperclass();
            }
        }
        return factor;
    }

    /**
     * Relationship level matching.
     */
    public static final class Match implements Scalar<Boolean> {

        /**
         * Relationship level.
         */
        private final int val;

        /**
         * Ctor.
         * @param level Relationship level
         */
        public Match(final int level) {
            this.val = level;
        }

        @Override
        public Boolean value() {
            return this.val < InheritanceDistance.NO_MATCH;
        }
    }
}
