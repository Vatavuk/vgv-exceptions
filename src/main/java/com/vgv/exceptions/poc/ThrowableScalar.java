package com.vgv.exceptions.poc;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface ThrowableScalar<T, E extends Exception> {

    T value() throws E;
}
