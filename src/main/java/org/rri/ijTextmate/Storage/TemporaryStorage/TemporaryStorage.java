package org.rri.ijTextmate.Storage.TemporaryStorage;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service(Service.Level.PROJECT)
public final class TemporaryStorage {
    private final Map<String, TemporaryMapPointerToLanguage> map = new HashMap<>();
    private final Object mutex = new Object();

    public TemporaryStorage() {
    }

    public @NotNull TemporaryMapPointerToLanguage get(String key) {
        synchronized (mutex) {
            TemporaryMapPointerToLanguage temporaryMap = map.get(key);
            if (temporaryMap == null) {
                temporaryMap = new TemporaryMapPointerToLanguage();
                map.put(key, temporaryMap);
            }
            return temporaryMap;
        }
    }

    public boolean contains(String key) {
        synchronized (mutex) {
            return map.get(key) != null;
        }
    }

    public @NotNull @UnmodifiableView Set<Map.Entry<String, TemporaryMapPointerToLanguage>> entrySet() {
        return Collections.unmodifiableSet(map.entrySet());
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
