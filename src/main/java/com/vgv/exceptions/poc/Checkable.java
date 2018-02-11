package com.vgv.exceptions.poc;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface Checkable {

    /**
     * Execute scalar through exception handling.
     * @param scalar Scalar
     * @param <T> Scalar type
     * @return Scalar value
     * @throws Exception Exception
     */
    <T, E extends Exception> T exec(ThrowableScalar<T, E> scalar) throws E;

    /**
     * Execute void procedure through exception handling.
     * @param proc Proc
     * @throws Exception Exception
     */
    <E extends Exception> void exec(ThrowableVoidProc<E> proc) throws E;
}
