package com.vgv.exceptions;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface Catchable {

    void handle(Exception exception);

    boolean supports(Exception exception);
}
