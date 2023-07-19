package com.razerford.ijTextmate.UnInject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.razerford.ijTextmate.Constants;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.NotNull;

public class UnInjectLanguage {
    public static void uninject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguage language, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> removeInjectionPlace(host));
    }

    private static void removeInjectionPlace(PsiLanguageInjectionHost host) {
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
            host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, null);
            host.getManager().dropPsiCaches();
        }
    }
}
