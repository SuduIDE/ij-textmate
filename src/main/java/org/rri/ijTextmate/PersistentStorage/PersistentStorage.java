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

import java.lang.reflect.Type;
import java.util.*;

@State(name = "PersistentStorage", storages = @Storage("PersistentStorage.xml"))
public class PersistentStorage implements PersistentStateComponent<PersistentStorage.MapFileToSetElement> {
    private MapFileToSetElement myMapToSetElement = new MapFileToSetElement();

    @Override
    public @NotNull MapFileToSetElement getState() {
        return myMapToSetElement;
    }

    @Override
    public void loadState(@NotNull MapFileToSetElement state) {
        myMapToSetElement = state;
    }

    public static PersistentStorage getInstance(@NotNull Project project) {
        return project.getService(PersistentStorage.class);
    }

    public static class MapFileToSetElement {
        private final Object mutex = new Object();

        @Property
        @OptionTag(converter = ConverterMapFileToSetElement.class)
        private final Map<String, SetElement> map = new HashMap<>();

        public MapFileToSetElement() {
        }

        public SetElement get(String key) {
            synchronized (mutex) {
                SetElement setElement = map.get(key);
                if (setElement == null) {
                    setElement = new SetElement();
                    map.put(key, setElement);
                }
                return setElement;
            }
        }

        public SetElement put(String key, SetElement value) {
            synchronized (mutex) {
                return map.put(key, value);
            }
        }

        public int size() {
            synchronized (mutex) {
                return map.size();
            }
        }

        public void clear() {
            synchronized (mutex) {
                map.clear();
            }
        }
    }

    public static class ConverterMapFileToSetElement extends Converter<Map<String, SetElement>> {
        @Override
        public @Nullable Map<String, SetElement> fromString(@NotNull String value) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(SetElement.class, new SetElement.SetElementAdapter())
                    .registerTypeAdapter(PlaceInjection.class, new PlaceInjection.PlaceInjectionAdapter())
                    .create();
            Type collectionType = new TypeToken<HashMap<String, SetElement>>() {
            }.getType();
            return gson.fromJson(value, collectionType);
        }

        @Override
        public @Nullable String toString(@NotNull Map<String, SetElement> value) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(SetElement.class, new SetElement.SetElementAdapter())
                    .registerTypeAdapter(PlaceInjection.class, new PlaceInjection.PlaceInjectionAdapter())
                    .create();
            return gson.toJson(value);
        }
    }
}
