package org.rri.ijTextmate.Inject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.RootMultipleInjectionStrategy;

public class InjectLanguageMultiplePlace extends AbstractInjectLanguage {
    public static InjectLanguageMultiplePlace INSTANCE = new InjectLanguageMultiplePlace();

    public String getIdentifier() {
        return "Inject where used";
    }

    @Override
    protected void addInjectionPlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project) {
        PsiElement psiElement = host.getParent();
        psiElement = PsiTreeUtil.getChildOfType(psiElement, PsiNamedElement.class);

        if (psiElement == null) {
            addInjectionPlace(host, languageID, psiFile, project);
            return;
        }

        addInjectionPlace(host, languageID, psiFile, project, new RootMultipleInjectionStrategy());
    }
}
