package com.vgv.exceptions;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.cactoos.Scalar;

/**
 * Exception control decorator that does not throw checked {@link Exception}.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Vedran Grgo Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 0.28.2
 */
public final class UncheckedTry implements Checkable<Exception> {

    private final Checkable<? extends Exception> origin;

    public UncheckedTry(final Checkable<? extends Exception> checkable) {
        this.origin = checkable;
    }

    @Override
    public <T> T exec(final Scalar<T> scalar) {
        try {
            return this.origin.exec(scalar);
        } catch (final Exception exp) {
            throw new UncheckedIOException(new IOException(exp));
        }
    }

    @Override
    public void exec(final VoidProc proc) {
        try {
            this.origin.exec(proc);
        } catch (final Exception exp) {
            throw new UncheckedIOException(new IOException(exp));
        }
    }
}
