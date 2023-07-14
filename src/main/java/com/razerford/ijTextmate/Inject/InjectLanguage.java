package com.razerford.ijTextmate.Inject;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.razerford.ijTextmate.TemporaryEntity.MyTemporaryLanguageInjectionSupport;
import com.razerford.ijTextmate.TemporaryEntity.MyTemporaryPlacesRegistry;
import org.intellij.plugins.intelliLang.Configuration;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.intellij.plugins.intelliLang.inject.LanguageInjectionSupport;
import org.intellij.plugins.intelliLang.inject.TemporaryPlacesRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InjectLanguage {
    public static void inject(PsiLanguageInjectionHost host, InjectedLanguage language, Project project) {
        InjectedLanguage prevLanguage = host.getUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE);
        SmartPsiElementPointer<PsiLanguageInjectionHost> pointer = SmartPointerManager.getInstance(project).createSmartPsiElementPointer(host);
        MyTemporaryPlacesRegistry.TemporaryPlace prevPlace = new MyTemporaryPlacesRegistry.TemporaryPlace(prevLanguage, pointer);
        MyTemporaryPlacesRegistry.TemporaryPlace place = new MyTemporaryPlacesRegistry.TemporaryPlace(language, pointer);
        WriteCommandAction.runWriteCommandAction(project, () -> addInjectionPlace(place, project));
    }

    private static void addInjectionPlace(MyTemporaryPlacesRegistry.@NotNull TemporaryPlace place, Project project) {
        PsiLanguageInjectionHost host = place.psiElementPointer.getElement();
        if (host == null) return;
        InjectedLanguageManager.getInstance(project).enumerate(host, (injectedPsi, places) -> {
            injectedPsi.putUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE, place.language);
        });
        host.putUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE, place.language);
        host.getManager().dropPsiCaches();
    }
}
