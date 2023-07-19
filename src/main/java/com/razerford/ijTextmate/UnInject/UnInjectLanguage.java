package com.razerford.ijTextmate.UnInject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;

public class UnInjectLanguage {
    public static void unInject(@NotNull PsiLanguageInjectionHost host, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> removeInjectionPlace(host));
    }

    private static void removeInjectionPlace(PsiLanguageInjectionHost host) {
        InjectorHelper.resolveInjectLanguage(host, null);
    }
}
