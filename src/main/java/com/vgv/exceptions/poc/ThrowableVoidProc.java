package com.vgv.exceptions.poc;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface ThrowableVoidProc<E extends Exception> {

    /**
     * Execute it.
     * @throws Exception Exception
     */
    void exec() throws E;
}
