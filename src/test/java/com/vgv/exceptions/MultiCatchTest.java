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
 * Test for {@link MultiCatch}.
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class MultiCatchTest {

    /**
     * First specified catch blocks supports catching of FileNotFoundException.
     */
    @Test
    public void supportsCatchingOfIoException() {
        MatcherAssert.assertThat(
            new MultiCatch(
                new Catch(
                    IOException.class, exp -> new FakeOperations().exec()
                ),
                new Catch(
                    RuntimeException.class, exp -> new FakeOperations().exec()
                )
            ).supports(new FileNotFoundException("msg")),
            Matchers.equalTo(true)
        );
    }

    /**
     * Both catch blocks do not support cathcing of FileNotFoundException.
     */
    @Test
    public void doesntSupportCatchingOfIoException() {
        MatcherAssert.assertThat(
            new MultiCatch(
                new Catch(
                    CustomException.class, exp -> new FakeOperations().exec()
                ),
                new Catch(
                    RuntimeException.class, exp -> new FakeOperations().exec()
                )
            ).supports(new FileNotFoundException("not found")),
            Matchers.equalTo(false)
        );
    }

    /**
     * Handle custom IOException.
     */
    @Test
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public void handlesCustomIoException() {
        final FakeOperations operations = new FakeOperations();
        new MultiCatch(
            new Catch(
                Exception.class, exp -> {
                throw new RuntimeException();
            }),
            new Catch(IOException.class, exp -> operations.exec())
        ).handle(new CustomIoException());
        MatcherAssert.assertThat(
            operations.isExecuted(),
            Matchers.equalTo(true)
        );
    }
}
