package org.rri.ijTextmate.Storage.PersistentStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@State(name = "PersistentStorage", storages = @Storage("PersistentStorageInjectSense.xml"))
public class PersistentStoragePrevious implements PersistentStateComponent<PersistentStoragePrevious.MapFileToSetElement> {
    private MapFileToSetElement myMapToSetElement = new MapFileToSetElement();
    private final Project project;

    public PersistentStoragePrevious(@NotNull Project project) {
        this.project = project;
    }

    public SetElement getSetElementAndClear(String relativePath) {
        SetElement setElement = myMapToSetElement.get(relativePath);
        myMapToSetElement.put(relativePath, new SetElement());
        return setElement;
    }

    @Override
    public @NotNull MapFileToSetElement getState() {
        for (Map.Entry<String, TemporaryMapPointerToLanguage> entry : TemporaryStorage.getInstance(project).entrySet()) {
            SetElement setElement = myMapToSetElement.get(entry.getKey());
            setElement.clear();

            for (var entryInner : entry.getValue().getMap().entrySet()) {
                String language = entryInner.getValue();
                PsiLanguageInjectionHost psiElement = entryInner.getKey().getElement();
                if (psiElement == null || !psiElement.isValidHost()) continue;
                setElement.add(new PlaceInjection(language, psiElement.getTextRange()));
            }
        }
        return myMapToSetElement;
    }

    @Override
    public void loadState(@NotNull MapFileToSetElement state) {
        myMapToSetElement = state;
    }

/*
    Uncomment if the class will be used. Add projectService to plugin.xml

    public static PersistentStoragePrevious getInstance(@NotNull Project project) {
        return project.getService(PersistentStoragePrevious.class);
    }
*/

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

        @SuppressWarnings("UnusedReturnValue")
        public SetElement put(String key, SetElement value) {
            synchronized (mutex) {
                return map.put(key, value);
            }
        }

        @SuppressWarnings("unused")
        public int size() {
            synchronized (mutex) {
                return map.size();
            }
        }

        @SuppressWarnings("unused")
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