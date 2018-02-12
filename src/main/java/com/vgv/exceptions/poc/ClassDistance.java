package com.vgv.exceptions.poc;

import org.cactoos.Scalar;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class ClassDistance implements Scalar<Integer> {

    private final Class<?> base;

    private final Class<?> comparing;

    public ClassDistance(final Class<?> from, final Class<?> towards) {
        this.base = from;
        this.comparing = towards;
    }

    @Override
    public Integer value() {
        int factor = -1;
        if (this.comparing.equals(this.base)) {
            factor = 999;
        } else {
            Class<?> sclass = this.base.getSuperclass();
            int idx = 0;
            while (!sclass.equals(Object.class)) {
                idx += 1;
                if(sclass.equals(this.comparing)) {
                    factor = idx;
                    break;
                }
                sclass = sclass.getSuperclass();
            }
        }
        return factor;
    }
}
