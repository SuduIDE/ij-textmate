package org.rri.ijTextmate.Inject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

public abstract class AbstractInjectLanguage {
    public void inject(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> addInjectionPlace(host, languageID, psiFile, project));
    }

    public abstract PsiLanguageInjectionHost getHost(PsiLanguageInjectionHost host);

    private void addInjectionPlace(PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project) {
        host = getHost(host);
        if (host == null) return;

        String relativePath = InjectorHelper.gitRelativePath(project, psiFile).toString();
        TemporaryMapPointerToLanguage mapPointerToLanguage = TemporaryStorage.getInstance(project).get(relativePath);
        SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer = SmartPointerManager.createPointer(host);
        TemporaryPlaceInjection temporaryPlaceInjection = new TemporaryPlaceInjection(psiElementPointer, languageID);
        mapPointerToLanguage.add(temporaryPlaceInjection);

        psiFile.putUserData(Constants.MY_LANGUAGE_INJECTED, new Object());

        host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, temporaryPlaceInjection);
        host.getManager().dropPsiCaches();
    }
}