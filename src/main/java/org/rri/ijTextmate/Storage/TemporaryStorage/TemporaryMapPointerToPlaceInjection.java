package org.rri.ijTextmate.Storage.TemporaryStorage;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TemporaryMapPointerToPlaceInjection extends AbstractMap<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection> {
    private final Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection> map = new ConcurrentHashMap<>();

    public TemporaryMapPointerToPlaceInjection() {
    }

    @SuppressWarnings("UnusedReturnValue")
    public TemporaryPlaceInjection put(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        return map.put(temporaryPlaceInjection.hostPointer, temporaryPlaceInjection);
    }

    @SuppressWarnings("UnusedReturnValue")
    public TemporaryPlaceInjection remove(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        return map.remove(temporaryPlaceInjection.hostPointer);
    }

    public TemporaryPlaceInjection get(PsiLanguageInjectionHost psiLanguageInjectionHost) {
        return map.get(SmartPointerManager.createPointer(psiLanguageInjectionHost));
    }

    @Override
    public TemporaryPlaceInjection remove(Object key) {
        return map.remove(key);
    }

    @Override
    public TemporaryPlaceInjection get(Object key) {
        return map.get(key);
    }

    @NotNull
    @Override
    public Set<SmartPsiElementPointer<PsiLanguageInjectionHost>> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Set<Entry<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection>> entrySet() {
        return map.entrySet();
    }
}
