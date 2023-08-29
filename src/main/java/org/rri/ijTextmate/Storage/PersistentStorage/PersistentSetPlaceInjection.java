package org.rri.ijTextmate.Storage.PersistentStorage;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PersistentSetPlaceInjection extends AbstractSet<PersistentPlaceInjection> {
    private final Set<PersistentPlaceInjection> set = ConcurrentHashMap.newKeySet();

    public PersistentSetPlaceInjection() {
    }

    @Override
    public @NotNull Iterator<PersistentPlaceInjection> iterator() {
        return new Iterator<>() {
            private final Iterator<PersistentPlaceInjection> iterator = set.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public PersistentPlaceInjection next() {
                return iterator.next();
            }
        };
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean add(PersistentPlaceInjection place) {
        return set.add(place);
    }

    @Override
    public boolean remove(Object place) {
        return set.remove(place);
    }

    @Override
    public void clear() {
        set.clear();
    }
}
