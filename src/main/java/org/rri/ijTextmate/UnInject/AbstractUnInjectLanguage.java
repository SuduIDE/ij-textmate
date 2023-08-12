package org.rri.ijTextmate.UnInject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.rri.ijTextmate.Constants;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

public abstract class AbstractUnInjectLanguage {
    public void unInject(@NotNull PsiLanguageInjectionHost host, TemporaryPlaceInjection temporaryPlaceInjection, PsiFile psiFile, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> removeInjectionPlace(host, temporaryPlaceInjection, psiFile, project));
    }

    @SuppressWarnings("UnusedParameters")
    private void removeInjectionPlace(@NotNull PsiLanguageInjectionHost host, TemporaryPlaceInjection temporaryPlaceInjection, PsiFile psiFile, Project project) {
        TemporaryPlaceInjection saved = host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);
        if (saved == null) return;
        saved.delete();
    }
}
