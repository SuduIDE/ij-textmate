package org.rri.ijTextmate.Storage.PersistentStorage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.Interfaces.ConverterElement;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.*;

@State(name = "PersistentStorage", storages = @Storage("PersistentStorageInjectSense.xml"))
public class PersistentStorage implements PersistentStateComponent<Element> {
    private final MapFileToSetElement mapToSetPlaceInjection = new MapFileToSetElement();
    private final Project project;

    public PersistentStorage(@NotNull Project project) {
        this.project = project;
    }

    public PersistentSetPlaceInjection getSetPlaceInjectionAndClear(String relativePath) {
        PersistentSetPlaceInjection persistentSetPlaceInjection = mapToSetPlaceInjection.get(relativePath);
        mapToSetPlaceInjection.put(relativePath, new PersistentSetPlaceInjection());
        return persistentSetPlaceInjection;
    }

    @Override
    public @NotNull Element getState() {
        for (Map.Entry<String, TemporaryMapPointerToPlaceInjection> entry : TemporaryStorage.getInstance(project).entrySet()) {
            PersistentSetPlaceInjection persistentSetPlaceInjection = mapToSetPlaceInjection.get(entry.getKey());
            persistentSetPlaceInjection.clear();

            for (var entryInner : entry.getValue().entrySet()) {
                TemporaryPlaceInjection temporaryPlaceInjection = entryInner.getValue();
                PsiLanguageInjectionHost psiElement = entryInner.getKey().getElement();
                if (psiElement == null || !psiElement.isValidHost()) continue;
                persistentSetPlaceInjection.add(new PersistentPlaceInjection(temporaryPlaceInjection.languageID, psiElement.getTextRange(), temporaryPlaceInjection.getStrategyIdentifier()));
            }
        }
        return mapToSetPlaceInjection.toElement();
    }

    @Override
    public void loadState(@NotNull Element state) {
        mapToSetPlaceInjection.fromElement(state);
    }

    public static PersistentStorage getInstance(@NotNull Project project) {
        return project.getService(PersistentStorage.class);
    }

    public static class MapFileToSetElement extends AbstractMap<String, PersistentSetPlaceInjection> implements ConverterElement {
        private static final String NAME = "PersistentStorage";
        private static final String PATH = "path";
        private static final String FILE = "file";
        private Map<String, PersistentSetPlaceInjection> map = new Hashtable<>();

        public MapFileToSetElement() {
        }

        public MapFileToSetElement(Map<String, PersistentSetPlaceInjection> map) {
            this.map = map;
        }

        public boolean fromElement(final @NotNull Element root) {
            map.clear();
            for (Element element : root.getChildren()) {
                PersistentSetPlaceInjection persistentSetPlaceInjection = new PersistentSetPlaceInjection();
                for (Element placeJDOM : element.getChildren()) {
                    PersistentPlaceInjection persistentPlaceInjection = new PersistentPlaceInjection();
                    if (persistentPlaceInjection.fromElement(placeJDOM)) {
                        persistentSetPlaceInjection.add(persistentPlaceInjection);
                    }
                }
                map.put(element.getAttribute(PATH).getValue(), persistentSetPlaceInjection);
            }
            return true;
        }

        public Element toElement() {
            Element root = new Element(NAME);
            for (Map.Entry<String, PersistentSetPlaceInjection> entry : map.entrySet()) {
                if (entry.getValue().isEmpty()) continue;
                Element element = new Element(FILE).setAttribute(PATH, entry.getKey());

                for (PersistentPlaceInjection place : entry.getValue()) {
                    if (place.languageId.isEmpty()) continue;
                    element.addContent(place.toElement());
                }

                if (!element.getContent().isEmpty()) root.addContent(element);
            }
            return root;
        }

        @Override
        public PersistentSetPlaceInjection get(Object key) {
            PersistentSetPlaceInjection persistentSetPlaceInjection = map.get(key);
            if (persistentSetPlaceInjection == null && key instanceof String str) {
                persistentSetPlaceInjection = new PersistentSetPlaceInjection();
                map.put(str, persistentSetPlaceInjection);
            }
            return persistentSetPlaceInjection;
        }

        @Override
        public PersistentSetPlaceInjection put(String key, PersistentSetPlaceInjection value) {
            return map.put(key, value);
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public Set<Map.Entry<String, PersistentSetPlaceInjection>> entrySet() {
            return map.entrySet();
        }
    }
}
