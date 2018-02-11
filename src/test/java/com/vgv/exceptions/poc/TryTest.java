package com.vgv.exceptions.poc;

import com.vgv.exceptions.Catch;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class TryTest {

    @Test
    public void tmp() throws Exception {
        new Try(
            new Catch(
                IOException.class,
                exp -> System.out.println("IO excpetion")
            ),
            new Catch(
                FileNotFoundException.class,
                xp -> System.out.println("FileNotFoundException excpetion")
            )
        ).exec(() -> { throw new FileNotFoundException(); });
    }

    public void method() throws IOException {
        throw new IOException();
    }

    private static class CustomException extends Exception {
        CustomException(Exception exception) {
            super(exception);
        }
    }
}
