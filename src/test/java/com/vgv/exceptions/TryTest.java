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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Try}.
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class TryTest {

    /**
     * Handle checked exception.
     */
    @Test
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void handlesCheckedException() {
        final FakeOperations operations = new FakeOperations();
        try {
            new Try(
                new Catch(
                    IOException.class,
                    e -> operations.exec()
                )
            ).exec(
                () -> {
                    throw new FileNotFoundException("file not found");
                });
                // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exp) {
            TryTest.doNothing();
        }
        MatcherAssert.assertThat(
            operations.isExecuted(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Handle runtime exception.
     * @throws Exception Exception
     */
    @Test
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void handlesUncheckedException() throws Exception {
        final FakeOperations operations = new FakeOperations();
        try {
            new Try(
                new Catch(
                    IllegalStateException.class,
                    e -> operations.exec()
                )
            ).exec(this::throwRuntimeException);
                // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception exp) {
            TryTest.doNothing();
        }
        MatcherAssert.assertThat(
            operations.isExecuted(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Execute finally block.
     * @throws Exception Exception
     */
    @Test
    public void executesFinallyBlock() throws Exception {
        final FakeOperations operations = new FakeOperations();
        new Try().with(new Finally(operations::exec))
            .exec(() -> new FakeOperations().exec());
        MatcherAssert.assertThat(
            operations.isExecuted(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Scalar execution returns result and finally block is triggered.
     * @throws Exception Exception
     */
    @Test
    public void getsScalarResultAndExecutesFinallyBlock() throws Exception {
        final FakeOperations operations = new FakeOperations();
        final int expected = 1;
        final int result = new Try().with(
            new Finally(operations::exec)
        ).exec(() -> 1);
        MatcherAssert.assertThat(
            result,
            Matchers.equalTo(expected)
        );
        MatcherAssert.assertThat(
            operations.isExecuted(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Scalar execution throws Exception and is mapped to IOException.
     * @throws IOException IOException
     */
    @Test(expected = IOException.class)
    public void scalarExecutionThrowsIoException()
        throws IOException {
        new Try().with(new Throws<>(IOException::new))
            .exec(
                () -> {
                    throw new Exception("exp");
                });
    }

    /**
     * Procedure execution throws Exception and is mapped to IOException.
     * @throws IOException IOException
     */
    @Test(expected = IOException.class)
    public void procedureExecutionThrowsIoException()
        throws IOException {
        new Try().with(new Throws<>(IOException::new))
            .exec(this::throwException);
    }

    /**
     * Procedure execution throws runtime exception and goes through.
     * @throws IOException IOException
     */
    @Test(expected = IllegalStateException.class)
    public void procedureExecutionThrowsRuntimeException()
        throws IOException {
        new Try().with(new Throws<>(IOException::new))
            .exec(this::throwRuntimeException);
    }

    /**
     * Scalar execution throws runtime exception and goes through.
     * @throws IOException IOException
     */
    @Test(expected = IllegalStateException.class)
    public void scalarExecutionThrowsRuntimeExecution() throws IOException {
        new Try().with(new Throws<>(IOException::new))
            .exec(
                () -> {
                    throw new IllegalStateException("illegal");
                });
    }

    /**
     * Try/catch/finally execution.
     */
    @Test
    public void fullExceptionControl() {
        final FakeOperations first = new FakeOperations();
        final FakeOperations second = new FakeOperations();
        try {
            new Try(
                new Catch(
                    IllegalStateException.class,
                    e -> first.exec()
                )
            ).with(
                new Finally(second::exec),
                new Throws<>(CustomException::new)
            )
                .exec(
                    () -> {
                        throw new IllegalStateException("msg");
                    });
        } catch (final CustomException exp) {
            TryTest.doNothing();
        }
        MatcherAssert.assertThat(
            first.isExecuted(),
            Matchers.equalTo(true)
        );
        MatcherAssert.assertThat(
            second.isExecuted(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Throw runtime exception.
     */
    private void throwRuntimeException() {
        throw new IllegalStateException("illegal state");
    }

    /**
     * Throw checked exception.
     * @throws Exception Exception
     */
    private void throwException() throws Exception {
        throw new Exception("exception");
    }

    /**
     * Do nothing.
     * @return Int Int
     */
    private static int doNothing() {
        return 1;
    }
}
