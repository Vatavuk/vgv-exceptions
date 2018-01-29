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
 * TryTest.
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class TryTest {

    @Test
    public void handlesCheckedException() throws Exception {
        final FakeOperations operations = new FakeOperations();
        try {
            new Try(
                new Catch(
                    IOException.class,
                    e -> operations.exec()
                )
            ).exec(() -> {
                throw new FileNotFoundException("file not found");
            });
        } catch (final Exception e) {
        }
        MatcherAssert.assertThat(
            operations.isExecuted(),
            Matchers.equalTo(true)
        );
    }

    @Test
    public void handlesUncheckedException() throws Exception {
        final FakeOperations operations = new FakeOperations();
        try {
            new Try(
                new Catch(
                    IllegalStateException.class,
                    e -> operations.exec()
                )
            ).exec(this::throwRuntimeException);
        } catch (final Exception e) {
        }
        MatcherAssert.assertThat(
            operations.isExecuted(),
            Matchers.equalTo(true)
        );
    }

    @Test
    public void executesFinallyblock() throws Exception {
        final FakeOperations operations = new FakeOperations();
        new Try().with(operations::exec).exec(() -> new FakeOperations().exec());
    }

    @Test
    public void getsFunctionResultAndExecutesFinallyBlock() throws Exception {
        final FakeOperations operations = new FakeOperations();
        final int expected = 1;
        final int result = new Try().with(
            new Finally(operations::exec)).exec(() -> 1);
        MatcherAssert.assertThat(
            result,
            Matchers.equalTo(expected)
        );
    }

    @Test(expected = IOException.class)
    public void mapsExceptionToIOExceptionForScalarExecution()
        throws IOException {
        new Try().with(new Throws<>(IOException::new))
            .exec(() -> {
                throw new Exception("exception");
            });
    }

    @Test(expected = IOException.class)
    public void mapsExceptionToIOExceptionForProcedureExecution()
        throws IOException {
        new Try().with(new Throws<>(IOException::new))
            .exec(this::throwException);
    }

    @Test(expected = IllegalStateException.class)
    public void runtimeExceptionGoesOutForProcedureExecution()
        throws IOException{
        new Try().with(new Throws<>(IOException::new))
            .exec(this::throwRuntimeException);
    }

    @Test(expected = IllegalStateException.class)
    public void runtimeExceptionGoesOutForScalarExecution() throws IOException{
        new Try().with(new Throws<>(IOException::new))
            .exec(() -> {
                throw new IllegalStateException("illegal");
            });
    }

    private void throwRuntimeException() {
        throw new IllegalStateException("illegal state");
    }

    private void throwException() throws Exception {
        throw new Exception("exception");
    }
}
