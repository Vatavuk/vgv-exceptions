package com.vgv.exceptions.poc;

import java.util.Comparator;
import org.cactoos.list.ListOf;

/**
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class MultiCatch implements CatchBlocks {

    private final Iterable<CatchBlock> blocks;

    public MultiCatch(CatchBlock... blks) {
        this(new ListOf<>(blks));
    }

    public MultiCatch(final Iterable<CatchBlock> blks) {
        this.blocks = blks;
    }

    @Override
    public void handle(final Exception exception) {
        new ListOf<>(this.blocks).stream()
            .filter(block -> block.supports(exception))
            .max(Comparator.comparing(block -> block.matchingFactor(exception)))
            .ifPresent(block -> block.handle(exception));
    }

    @Override
    public boolean supports(final Exception exception) {
        return new ListOf<>(this.blocks)
            .stream().anyMatch(catchable -> catchable.supports(exception));
    }
}
