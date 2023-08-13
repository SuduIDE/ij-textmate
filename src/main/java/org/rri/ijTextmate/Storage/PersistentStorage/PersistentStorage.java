package org.rri.ijTextmate.Storage.PersistentStorage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.Interfaces.ConverterElement;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.*;

@State(name = "PersistentStorage", storages = @Storage("PersistentStorageInjectSense.xml"))
public class PersistentStorage implements PersistentStateComponent<Element> {
    private final MapFileToSetElement myMapToSetElement = new MapFileToSetElement();
    private final Project project;

    public PersistentStorage(@NotNull Project project) {
        this.project = project;
    }

    public SetElement getSetElementAndClear(String relativePath) {
        SetElement setElement = myMapToSetElement.get(relativePath);
        myMapToSetElement.put(relativePath, new SetElement());
        return setElement;
    }

    @Override
    public @NotNull Element getState() {
        for (Map.Entry<String, TemporaryMapPointerToLanguage> entry : TemporaryStorage.getInstance(project).entrySet()) {
            SetElement setElement = myMapToSetElement.get(entry.getKey());
            setElement.clear();

            for (var entryInner : entry.getValue().getMap().entrySet()) {
                TemporaryPlaceInjection temporaryPlaceInjection = entryInner.getValue();
                PsiLanguageInjectionHost psiElement = entryInner.getKey().getElement();
                if (psiElement == null || !psiElement.isValidHost()) continue;
                setElement.add(new PlaceInjection(temporaryPlaceInjection.languageID, psiElement.getTextRange(), temporaryPlaceInjection.getStrategyIdentifier()));
            }
        }
        return myMapToSetElement.toElement();
    }

    @Override
    public void loadState(@NotNull Element state) {
        myMapToSetElement.fromElement(state);
    }

    public static PersistentStorage getInstance(@NotNull Project project) {
        return project.getService(PersistentStorage.class);
    }

    public static class MapFileToSetElement implements ConverterElement {
        private static final String NAME = "PersistentStorage";
        private static final String PATH = "path";
        private static final String FILE = "file";
        private final Object mutex = new Object();

        private Map<String, SetElement> map = new HashMap<>();

        public MapFileToSetElement() {
        }

        public MapFileToSetElement(Map<String, SetElement> map) {
            this.map = map;
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

        public boolean fromElement(final @NotNull Element root) {
            map.clear();
            for (Element element : root.getChildren()) {
                SetElement setElement = new SetElement();
                for (Element placeJDOM : element.getChildren()) {
                    PlaceInjection placeInjection = new PlaceInjection();
                    if (placeInjection.fromElement(placeJDOM)) setElement.add(placeInjection);
                }
                map.put(element.getAttribute(PATH).getValue(), setElement);
            }
            return true;
        }

        public Element toElement() {
            Element root = new Element(NAME);
            for (Map.Entry<String, SetElement> entry : map.entrySet()) {
                if (entry.getValue().isEmpty()) continue;
                Element element = new Element(FILE).setAttribute(PATH, entry.getKey());

                for (PlaceInjection place : entry.getValue()) {
                    if (place.languageId.isEmpty()) continue;
                    element.addContent(place.toElement());
                }

                if (!element.getContent().isEmpty()) root.addContent(element);
            }
            return root;
        }

        public Map<String, SetElement> getMap() {
            return Collections.unmodifiableMap(map);
        }
    }
}