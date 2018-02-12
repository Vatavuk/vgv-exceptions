package com.vgv.exceptions.poc;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface MappedTryBlock<E extends Exception> {

    /**
     * Execute scalar through exception handling.
     * @param scalar Scalar
     * @param <T> Scalar type
     * @return Scalar value
     * @throws Exception Exception
     */
    <T> T exec(ThrowableScalar<T, Exception> scalar) throws E;

    /**
     * Execute void procedure through exception handling.
     * @param proc Proc
     * @throws Exception Exception
     */
    void exec(ThrowableVoidProc<Exception> proc) throws E;
}
