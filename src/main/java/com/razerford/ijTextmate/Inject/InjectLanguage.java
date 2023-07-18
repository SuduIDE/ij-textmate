package com.razerford.ijTextmate.Inject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InjectLanguage {
    public static final Key<InjectedLanguage> MY_TEMPORARY_INJECTED_LANGUAGE = Key.create("MY_TEMPORARY_INJECTED_LANGUAGE");

    public static void inject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguage language, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> addInjectionPlace(language, host, project));
    }

    private static void addInjectionPlace(InjectedLanguage language, PsiLanguageInjectionHost host, Project project) {
        if (host == null) return;
        PsiElement element = host.getOriginalElement();
        if (element != null) element = element.getParent();
        PsiReference psiReference = getFirstReference(element);
        if (!(element instanceof PsiNameIdentifierOwner) && psiReference != null) {
            element = psiReference.resolve();
            PsiLanguageInjectionHost newHost = getHostFromElementRoot(element);
            host = (newHost == null) ? host : newHost;
        }
        if (host.isValidHost()) {
            host.putUserData(MY_TEMPORARY_INJECTED_LANGUAGE, language);
            host.getManager().dropPsiCaches();
        }
    }

    @Contract(pure = true)
    public static @Nullable PsiLanguageInjectionHost getHostFromElementRoot(PsiElement root) {
        if (root == null) return null;
        for (PsiElement element : root.getChildren()) {
            if (element instanceof PsiLanguageInjectionHost host) {
                return host;
            }
        }
        return null;
    }

    @Contract(pure = true)
    public static @Nullable PsiReference getFirstReference(PsiElement root) {
        if (root == null) return null;
        for (PsiElement element : root.getChildren()) {
            if (element instanceof PsiReference reference) {
                return reference;
            }
        }
        return null;
    }
}
