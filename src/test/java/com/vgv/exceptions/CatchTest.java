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
 * CatchTestTest.
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class CatchTest {

    /**
     * Catches FileNotFoundException and throws UncheckedIOException
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

//    /**
//     * Executes function without exception handling.
//     * @throws Exception Exception
//     */
//    @Test
//    public void executesWithoutExceptionHandling()
//        throws Exception {
//        final CatchTest.FakeOperations operations =
//            new CatchTest.FakeOperations();
//        new CatchTest(
//            Exception.class,
//            exp -> {
//                throw new IOException(exp);
//            }).valueOf(operations::valueOf);
//        MatcherAssert.assertThat(
//            operations.isExecuted(),
//            Matchers.equalTo(true)
//        );
//    }
//
//    /**
//     * Catches runtime exception and throws IOException.
//     * @throws Exception Exception
//     */
//    @Test(expected = IOException.class)
//    public void wrapsRuntimeExceptionToIoException() throws Exception {
//        new CatchTest(
//            IllegalStateException.class,
//            exp -> {
//                throw new IOException(exp);
//            }).valueOf(
//            () -> {
//                throw new IllegalStateException("illegal state");
//            });
//    }
//
//    /**
//     * Catches exception and throws runtime exception.
//     * @throws Exception Exception
//     */
//    @Test(expected = IllegalStateException.class)
//    public void wrapsExceptionToRuntimeException() throws Exception {
//        new CatchTest(
//            IOException.class,
//            exp -> {
//                throw new IllegalStateException(exp);
//            }).valueOf(
//            () -> {
//                throw new IOException("io exception");
//            });
//    }
//
//    /**
//     * No exception is handled.
//     * @throws Exception Exception
//     */
//    @Test(expected = FileNotFoundException.class)
//    public void doesntHandleThrownException() throws Exception {
//        new CatchTest(
//            IOException.class,
//            exp -> {
//                throw new IllegalStateException(exp);
//            }).valueOf(
//            () -> {
//                throw new FileNotFoundException("not found");
//            });
//    }

    /**
     * FakeOperations.
     */
    private static final class FakeOperations {

        /**
         * Is operation executed.
         */
        private boolean executed;

        /**
         * Ctor.
         */
        FakeOperations() {
            this.executed = false;
        }

        /**
         * Execute fake operation.
         */
        public void exec() {
            this.executed = true;
        }

        /**
         * Check if operation is executed.
         * @return Boolean Boolean
         */
        public boolean isExecuted() {
            return this.executed;
        }
    }
}
