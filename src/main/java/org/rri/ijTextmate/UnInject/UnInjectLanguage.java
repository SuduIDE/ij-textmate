package org.rri.ijTextmate.UnInject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

public class UnInjectLanguage {
    public static void unInject(@NotNull PsiLanguageInjectionHost host, TemporaryPlaceInjection temporaryPlaceInjection, PsiFile psiFile, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> removeInjectionPlace(host, temporaryPlaceInjection, psiFile, project));
    }

    private static void removeInjectionPlace(PsiLanguageInjectionHost host, TemporaryPlaceInjection temporaryPlaceInjection, PsiFile psiFile, Project project) {
        host = InjectorHelper.resolveHost(host);
        if (!host.isValidHost()) return;

        String relativePath = InjectorHelper.gitRelativePath(project, psiFile).toString();
        TemporaryMapPointerToLanguage mapPointerToLanguage = TemporaryStorage.getInstance(project).get(relativePath);
        mapPointerToLanguage.remove(temporaryPlaceInjection);

        host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, null);
        host.getManager().dropPsiCaches();
    }
}
