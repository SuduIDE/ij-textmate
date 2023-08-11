package org.rri.ijTextmate.Inject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

public class InjectLanguageOnePlace extends AbstractInjectLanguage {
    public static InjectLanguageOnePlace INSTANCE = new InjectLanguageOnePlace();

    public String getIdentifier() {
        return "Inject one place";
    }

    @Override
    protected void addInjectionPlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project) {
        if (!host.isValidHost()) return;
        SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer = SmartPointerManager.createPointer(host);

        TemporaryPlaceInjection temporaryPlaceInjection = new TemporaryPlaceInjection(psiElementPointer, languageID);

        String relativePath = InjectorHelper.getRelativePath(project, psiFile);
        TemporaryMapPointerToLanguage mapPointerToLanguage = TemporaryStorage.getInstance(project).get(relativePath);
        mapPointerToLanguage.add(temporaryPlaceInjection);

        putUserData(host, psiFile, temporaryPlaceInjection);
    }
}
