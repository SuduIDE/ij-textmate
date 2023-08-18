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
    private final Map<String, TemporaryMapPointerToPlaceInjection> map = new HashMap<>();
    private final Object mutex = new Object();

    public TemporaryStorage() {
    }

    public @NotNull TemporaryMapPointerToPlaceInjection get(String key) {
        synchronized (mutex) {
            TemporaryMapPointerToPlaceInjection temporaryMap = map.get(key);
            if (temporaryMap == null) {
                temporaryMap = new TemporaryMapPointerToPlaceInjection();
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

    public @NotNull @UnmodifiableView Set<Map.Entry<String, TemporaryMapPointerToPlaceInjection>> entrySet() {
        return Collections.unmodifiableSet(map.entrySet());
    }

    @SuppressWarnings("unused")
    public TemporaryMapPointerToPlaceInjection put(String key, TemporaryMapPointerToPlaceInjection value) {
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

    public static TemporaryStorage getInstance(@NotNull Project project) {
        return project.getService(TemporaryStorage.class);
    }
}
