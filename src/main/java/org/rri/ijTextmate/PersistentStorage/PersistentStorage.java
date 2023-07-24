package org.rri.ijTextmate.PersistentStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

@State(name = "PersistentStorage", storages = @Storage("PersistentStorage.xml"))
public class PersistentStorage implements PersistentStateComponent<PersistentStorage.SetElement> {
    private SetElement mySetElement = new SetElement();

    @Override
    public @NotNull SetElement getState() {
        return mySetElement;
    }

    @Override
    public void loadState(@NotNull SetElement state) {
        mySetElement = state;
    }

    public static PersistentStorage getInstance(@NotNull Project project) {
        return project.getService(PersistentStorage.class);
    }


    public static class SetElement extends AbstractSet<PlaceInjection> {
        private final Object mutex = new Object();

        @Property
        @OptionTag(converter = ConverterSetElement.class)
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
            synchronized (mutex){
                return set.remove(place);
            }
        }

        @Override
        public void clear() {
            synchronized (mutex){
                set.clear();
            }
        }
    }

    public static class ConverterSetElement extends Converter<Set<PlaceInjection>> {
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
}
