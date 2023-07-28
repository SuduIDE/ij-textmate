package org.rri.ijTextmate.Storage.PersistentStorage;

import com.google.gson.*;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;

public class SetElement extends AbstractSet<PlaceInjection> {
    private final Object mutex = new Object();

    private final Set<PlaceInjection> set = new HashSet<>();
    public static final Comparator<TextRange> COMPARATOR = (rangeLeft, rangeRight) -> {
        if (rangeLeft.intersects(rangeRight)) return 0;
        return rangeLeft.getStartOffset() - rangeRight.getStartOffset();
    };
    Map<TextRange, PlaceInjection> mapTextRange = new TreeMap<>(COMPARATOR);


    public SetElement() {
    }

    @Override
    public Iterator<PlaceInjection> iterator() {
        return new Iterator<>() {
            private final Iterator<PlaceInjection> iterator = set.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public PlaceInjection next() {
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
    public boolean add(PlaceInjection place) {
        synchronized (mutex) {
            PlaceInjection oldPlace = mapTextRange.put(place.textRange, place);
            if (oldPlace != null) set.remove(oldPlace);
            return set.add(place);
        }
    }

    public boolean contains(PlaceInjection place) {
        return set.contains(place);
    }

    public boolean contains(final int offset) {
        synchronized (mutex) {
            TextRange textRange = new TextRange(offset, offset);
            PlaceInjection placeInjection = mapTextRange.get(textRange);
            return set.contains(placeInjection);
        }
    }

    public boolean remove(final int offset) {
        synchronized (mutex) {
            TextRange textRange = new TextRange(offset, offset);
            PlaceInjection placeInjection = mapTextRange.remove(textRange);
            return set.remove(placeInjection);
        }
    }

    public boolean remove(@NotNull PlaceInjection place) {
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

    public static class SetElementAdapter implements JsonSerializer<SetElement>, JsonDeserializer<SetElement> {
        @Override
        public JsonElement serialize(@NotNull SetElement placeInjections, Type type, @NotNull JsonSerializationContext jsonSerializationContext) {
            JsonArray jsonArray = new JsonArray(placeInjections.size());
            if (placeInjections.isEmpty()) return null;
            for (PlaceInjection placeInjection : placeInjections) {
                JsonElement jsonElement = jsonSerializationContext.serialize(placeInjection);
                if (!jsonElement.isJsonNull()) jsonArray.add(jsonElement);
            }
            return jsonArray.isEmpty() ? null : jsonArray;
        }

        @Override
        public SetElement deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            SetElement setElement = new SetElement();
            if (!jsonElement.isJsonArray()) return setElement;
            JsonArray jsonElements = jsonElement.getAsJsonArray();
            for (JsonElement jsonElementOne : jsonElements) {
                PlaceInjection placeInjection = jsonDeserializationContext.deserialize(jsonElementOne, PlaceInjection.class);
                if (placeInjection != null) setElement.add(placeInjection);
            }
            return setElement;
        }
    }
}