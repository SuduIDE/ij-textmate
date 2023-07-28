package org.rri.ijTextmate.Storage.TemporaryStorage;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TemporaryMapPointerToLanguage {
    private final Object mutex = new Object();
    private final Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, String> map = new HashMap<>();

    public TemporaryMapPointerToLanguage() {
    }

    public String add(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        return synchronizedSupplier(() -> map.put(temporaryPlaceInjection.hostPointer, temporaryPlaceInjection.languageID));
    }

    public String remove(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        return synchronizedSupplier(() -> map.remove(temporaryPlaceInjection.hostPointer));
    }

    public String remove(SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer) {
        return synchronizedSupplier(() -> map.remove(psiElementPointer));
    }

    public String get(SmartPsiElementPointer<PsiLanguageInjectionHost> key) {
        return synchronizedSupplier(() -> map.get(key));
    }

    public String get(PsiLanguageInjectionHost psiLanguageInjectionHost) {
        return synchronizedSupplier(() ->  map.get(SmartPointerManager.createPointer(psiLanguageInjectionHost)));
    }

    public Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, String> getMap() {
        return Collections.unmodifiableMap(map);
    }

    private <T> T synchronizedSupplier(@NotNull Supplier<T> supplier) {
        synchronized (mutex) {
            return supplier.get();
        }
    }
}
