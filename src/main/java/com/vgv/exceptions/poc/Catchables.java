package com.vgv.exceptions.poc;

import com.vgv.exceptions.Catchable;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class Catchables {

    private final Iterable<Catchable> catchables;

    public Catchables(final Iterable<Catchable> chbls) {
        this.catchables = chbls;
    }

    public void handle(final Exception exception) {
        for (final Catchable catchable: this.catchables) {
            if (catchable.supports(exception)) {
                catchable.handle(exception);
                return;
            }
        }
    }

    public boolean supports(final Exception exception) {
        throw new UnsupportedOperationException("#supports()");
    }
}
