package com.razerford.ijTextmate.PersistentStorage;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;

import java.util.List;

public class MyTemporaryPlacesRegistry {
    private final List<TemporaryPlace> temporaryPlaces = ContainerUtil.createLockFreeCopyOnWriteList();

    public static class TemporaryPlace {
        public final String languageId;
        public int offset;

        public TemporaryPlace(String languageId, final int offset) {
            this.offset = offset;
            this.languageId = languageId;
        }
    }
}
