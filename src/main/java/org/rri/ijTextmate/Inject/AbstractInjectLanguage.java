package org.rri.ijTextmate.Inject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

public abstract class AbstractInjectLanguage {
    public abstract String getIdentifier();

    public void inject(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> addInjectionPlace(host, languageID, psiFile, project));
    }

    public abstract TemporaryPlaceInjection getTemporaryPlaceInjection(@NotNull PsiLanguageInjectionHost host, String languageID);

    private void addInjectionPlace(PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project) {
        TemporaryPlaceInjection temporaryPlaceInjection = getTemporaryPlaceInjection(host, languageID);
        if (temporaryPlaceInjection == null) return;

        String relativePath = InjectorHelper.getRelativePath(project, psiFile);
        TemporaryMapPointerToLanguage mapPointerToLanguage = TemporaryStorage.getInstance(project).get(relativePath);
        mapPointerToLanguage.add(temporaryPlaceInjection);

        host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, temporaryPlaceInjection);
        host.getManager().dropPsiCaches();

        psiFile.putUserData(Constants.MY_LANGUAGE_INJECTED, new Object());
    }
}
