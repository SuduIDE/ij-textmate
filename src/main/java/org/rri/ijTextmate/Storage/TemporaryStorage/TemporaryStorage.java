package org.rri.ijTextmate.Storage.TemporaryStorage;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Service(Service.Level.PROJECT)
public final class TemporaryStorage {
    private final Map<String, TemporaryMapPointerToLanguage> map = new HashMap<>();
    private final Object mutex = new Object();
    public TemporaryStorage() {
    }

    public @NotNull TemporaryMapPointerToLanguage get(String key) {
        synchronized (mutex) {
            TemporaryMapPointerToLanguage temporarySetPointer = map.get(key);
            if (temporarySetPointer == null) {
                temporarySetPointer = new TemporaryMapPointerToLanguage();
                map.put(key, temporarySetPointer);
            }
            return temporarySetPointer;
        }
    }

    public TemporaryMapPointerToLanguage put(String key, TemporaryMapPointerToLanguage value) {
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

    public static TemporaryStorage getInstance(@NotNull Project project) {
        return project.getService(TemporaryStorage.class);
    }
}
