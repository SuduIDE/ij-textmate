package com.razerford.ijTextmate.Inject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.razerford.ijTextmate.Constants;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.NotNull;

public class InjectLanguage {
    public static void inject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguage language, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> addInjectionPlace(language, host));
    }

    private static void addInjectionPlace(InjectedLanguage language, PsiLanguageInjectionHost host) {
        if (host == null) return;
        PsiElement element = host.getOriginalElement();
        if (element != null) element = element.getParent();
        PsiReference psiReference = InjectorHelper.getFirstReference(element);
        if (!(element instanceof PsiNameIdentifierOwner) && psiReference != null) {
            element = psiReference.resolve();
            PsiLanguageInjectionHost newHost = InjectorHelper.getHostFromElementRoot(element);
            host = (newHost == null) ? host : newHost;
        }
        if (host.isValidHost()) {
            host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, language);
            host.getManager().dropPsiCaches();
        }
    }
}
