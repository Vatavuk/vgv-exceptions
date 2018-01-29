package com.vgv.exceptions;

import java.util.function.Function;

/**
 * <p>
 * <b>Title: Throws </b>
 * </p>
 * <p>
 * <b> Description:
 * </b>
 * </p>
 * <p>
 * <b>Copyright:(</b> Copyright (c) ETK 2017
 * </p>
 * <p>
 * <b>Company:(</b> Ericsson Nikola Tesla d.d.
 * </p>
 * @author evedvat
 * @version PA1
 * <p>
 * <b>Version History:(</b>
 * </p>
 * <br>
 * PA1 29.1.2018.
 * @since 29.1.2018.
 */

public final class Throws<E extends Exception> implements
    Function<Exception, E> {

    private final Function<Exception, E> origin;

    public Throws(final Function<Exception, E> fun) {
        this.origin = fun;
    }

    @Override
    public E apply(final Exception exp) {
        return this.origin.apply(exp);
    }
}
