package com.razerford.ijTextmate.PersistentStorage;

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
import io.ktor.util.collections.ConcurrentSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Set;

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


    public static class SetElement {
        @Property
        @OptionTag(converter = ConverterSetElement.class)
        private final Set<TemporaryPlace> set = new ConcurrentSet<>();

        public SetElement() {
        }

        public boolean addElement(TemporaryPlace place) {
            return set.add(place);
        }

        public boolean contains(TemporaryPlace place) {
            return set.contains(place);
        }

        public boolean remove(TemporaryPlace place) {
            return set.remove(place);
        }

        public Set<TemporaryPlace> getElements() {
            return set;
        }
    }

    public static class ConverterSetElement extends Converter<Set<TemporaryPlace>> {
        @Override
        public @Nullable Set<TemporaryPlace> fromString(@NotNull String value) {
            GsonBuilder gson = new GsonBuilder();
            Type collectionType = new TypeToken<Set<TemporaryPlace>>() {
            }.getType();
            return gson.create().fromJson(value, collectionType);
        }

        @Override
        public @Nullable String toString(@NotNull Set<TemporaryPlace> value) {
            Writer writer = new StringWriter();
            Gson gson = new Gson();
            gson.toJson(gson.toJsonTree(value), writer);
            return writer.toString();
        }
    }
}
