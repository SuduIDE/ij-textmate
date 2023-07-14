package com.razerford.ijTextmate.TemporaryEntity;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;

import java.util.List;

public class MyTemporaryPlacesRegistry {
    private final List<TemporaryPlace> temporaryPlaces = ContainerUtil.createLockFreeCopyOnWriteList();

    public static class TemporaryPlace {
        public final InjectedLanguage language;
        public SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer;

        public TemporaryPlace(InjectedLanguage language, SmartPsiElementPointer<PsiLanguageInjectionHost> elementPointer) {
            this.psiElementPointer = elementPointer;
            this.language = language;
        }
    }
}
