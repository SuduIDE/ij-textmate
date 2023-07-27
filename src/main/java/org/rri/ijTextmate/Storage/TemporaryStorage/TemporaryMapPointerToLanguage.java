package org.rri.ijTextmate.Storage.TemporaryStorage;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TemporaryMapPointerToLanguage {
    private final Object mutex = new Object();
    private volatile long myModificationTracker;
    private final Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, String> map = new HashMap<>();

    public TemporaryMapPointerToLanguage() {
    }

    public String add(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        return map.put(temporaryPlaceInjection.hostPointer, temporaryPlaceInjection.languageID);
    }

    public String remove(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        return map.remove(temporaryPlaceInjection.hostPointer);
    }

    public String remove(SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer) {
        return map.remove(psiElementPointer);
    }

    public String get(SmartPsiElementPointer<PsiLanguageInjectionHost> key) {
        return map.get(key);
    }

    public String get(PsiLanguageInjectionHost psiLanguageInjectionHost) {
        var key = SmartPointerManager.createPointer(psiLanguageInjectionHost);
        return map.get(key);
    }
}
