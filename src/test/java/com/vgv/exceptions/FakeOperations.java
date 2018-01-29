package com.vgv.exceptions;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
class FakeOperations {

    /**
     * Is operation executed.
     */
    private boolean executed;

    /**
     * Ctor.
     */
    FakeOperations() {
        this.executed = false;
    }

    /**
     * Execute fake operation.
     */
    public void exec() {
        this.executed = true;
    }

    /**
     * Check if operation is executed.
     * @return Boolean Boolean
     */
    public boolean isExecuted() {
        return this.executed;
    }
}
