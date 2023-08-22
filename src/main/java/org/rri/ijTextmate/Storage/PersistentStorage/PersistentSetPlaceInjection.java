package org.rri.ijTextmate.Storage.PersistentStorage;

import java.util.*;

public class PersistentSetPlaceInjection extends AbstractSet<PersistentPlaceInjection> {
    private final Set<PersistentPlaceInjection> set = new HashSet<>();

    public PersistentSetPlaceInjection() {
    }

    @Override
    public Iterator<PersistentPlaceInjection> iterator() {
        return new Iterator<>() {
            private final Iterator<PersistentPlaceInjection> iterator = set.iterator();

            @Override
            public synchronized boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public synchronized PersistentPlaceInjection next() {
                return iterator.next();
            }
        };
    }

    @Override
    public synchronized int size() {
        return set.size();
    }

    @Override
    public synchronized boolean add(PersistentPlaceInjection place) {
        return set.add(place);
    }

    @Override
    public synchronized boolean remove(Object place) {
        return set.remove(place);
    }

    @Override
    public synchronized void clear() {
        set.clear();
    }
}
