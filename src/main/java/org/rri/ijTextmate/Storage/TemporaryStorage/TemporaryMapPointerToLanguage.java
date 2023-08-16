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
    private final Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection> map = new HashMap<>();

    public TemporaryMapPointerToLanguage() {
    }

    @SuppressWarnings("UnusedReturnValue")
    public TemporaryPlaceInjection add(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        return synchronizedSupplier(() -> map.put(temporaryPlaceInjection.hostPointer, temporaryPlaceInjection));
    }

    @SuppressWarnings("UnusedReturnValue")
    public TemporaryPlaceInjection remove(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        return synchronizedSupplier(() -> map.remove(temporaryPlaceInjection.hostPointer));
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public TemporaryPlaceInjection remove(SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer) {
        return synchronizedSupplier(() -> map.remove(psiElementPointer));
    }

    public TemporaryPlaceInjection get(SmartPsiElementPointer<PsiLanguageInjectionHost> key) {
        return synchronizedSupplier(() -> map.get(key));
    }

    public TemporaryPlaceInjection get(PsiLanguageInjectionHost psiLanguageInjectionHost) {
        return synchronizedSupplier(() -> map.get(SmartPointerManager.createPointer(psiLanguageInjectionHost)));
    }

    public Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection> getMap() {
        return Collections.unmodifiableMap(map);
    }

    private <T> T synchronizedSupplier(@NotNull Supplier<T> supplier) {
        synchronized (mutex) {
            return supplier.get();
        }
    }
}
