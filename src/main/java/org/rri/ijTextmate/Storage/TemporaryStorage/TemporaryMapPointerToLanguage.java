package org.rri.ijTextmate.Storage.TemporaryStorage;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPsiElementPointer;

import java.util.HashMap;
import java.util.Map;

public class TemporaryMapPointerToLanguage {
    private volatile long myModificationTracker;
    private Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, String> map = new HashMap<>();

    public TemporaryMapPointerToLanguage() {
    }
}
