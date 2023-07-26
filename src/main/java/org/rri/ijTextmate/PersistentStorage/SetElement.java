package org.rri.ijTextmate.PersistentStorage;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Type;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SetElement extends AbstractSet<PlaceInjection> {
    private final Object mutex = new Object();

    private final Set<PlaceInjection> set = new HashSet<>();

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
            return set.add(place);
        }
    }

    public boolean contains(PlaceInjection place) {
        return set.contains(place);
    }

    public boolean remove(PlaceInjection place) {
        synchronized (mutex) {
            return set.remove(place);
        }
    }

    @Override
    public void clear() {
        synchronized (mutex) {
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