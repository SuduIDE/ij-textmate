package org.rri.ijTextmate.Storage.PersistentStorage;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PersistentSetPlaceInjection extends AbstractSet<PersistentPlaceInjection> {
    private final Object mutex = new Object();

    private final Set<PersistentPlaceInjection> set = new HashSet<>();
    public static final Comparator<TextRange> COMPARATOR = (rangeLeft, rangeRight) -> {
        if (rangeLeft.intersects(rangeRight)) return 0;
        return rangeLeft.getStartOffset() - rangeRight.getStartOffset();
    };
    Map<TextRange, PersistentPlaceInjection> mapTextRange = new TreeMap<>(COMPARATOR);

    public PersistentSetPlaceInjection() {
    }

    @Override
    public Iterator<PersistentPlaceInjection> iterator() {
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
        synchronized (mutex) {
            return set.size();
        }
    }

    @Override
    public boolean add(PersistentPlaceInjection place) {
        synchronized (mutex) {
            PersistentPlaceInjection oldPlace = mapTextRange.put(place.textRange, place);
            if (oldPlace != null) set.remove(oldPlace);
            return set.add(place);
        }
    }

    @SuppressWarnings("unused")
    public boolean contains(PersistentPlaceInjection place) {
        return set.contains(place);
    }

    @SuppressWarnings("unused")
    public boolean contains(final int offset) {
        synchronized (mutex) {
            TextRange textRange = new TextRange(offset, offset);
            PersistentPlaceInjection persistentPlaceInjection = mapTextRange.get(textRange);
            return set.contains(persistentPlaceInjection);
        }
    }

    @SuppressWarnings("unused")
    public boolean remove(final int offset) {
        synchronized (mutex) {
            TextRange textRange = new TextRange(offset, offset);
            PersistentPlaceInjection persistentPlaceInjection = mapTextRange.remove(textRange);
            return set.remove(persistentPlaceInjection);
        }
    }

    @SuppressWarnings("unused")
    public boolean remove(@NotNull PersistentPlaceInjection place) {
        synchronized (mutex) {
            mapTextRange.remove(place.textRange);
            return set.remove(place);
        }
    }

    @Override
    public void clear() {
        synchronized (mutex) {
            mapTextRange.clear();
            set.clear();
        }
    }
}