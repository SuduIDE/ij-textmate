package org.rri.ijTextmate.PersistentStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StringWriter;
import java.io.Writer;
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

    public static class SetElementConverter extends Converter<Set<PlaceInjection>> {
        @Override
        public @Nullable Set<PlaceInjection> fromString(@NotNull String value) {
            GsonBuilder gson = new GsonBuilder();
            Type collectionType = new TypeToken<Set<PlaceInjection>>() {
            }.getType();
            return gson.create().fromJson(value, collectionType);
        }

        @Override
        public @Nullable String toString(@NotNull Set<PlaceInjection> value) {
            Writer writer = new StringWriter();
            Gson gson = new Gson();
            gson.toJson(gson.toJsonTree(value), writer);
            return writer.toString();
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