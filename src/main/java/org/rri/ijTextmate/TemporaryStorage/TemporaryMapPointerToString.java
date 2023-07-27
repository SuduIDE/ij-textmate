package org.rri.ijTextmate.TemporaryStorage;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPsiElementPointer;

import java.util.HashMap;
import java.util.Map;

public class TemporaryMapPointerToString {
    private volatile long myModificationTracker;
    private Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, String> map = new HashMap<>();

    public TemporaryMapPointerToString() {
    }
}
