package org.rri.ijTextmate.Inject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.RootMultipleInjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.SingleInjectionStrategy;

public class InjectLanguageMultiplePlace extends AbstractInjectLanguage {
    public static InjectLanguageMultiplePlace INSTANCE = new InjectLanguageMultiplePlace();

    public String getIdentifier() {
        return "Inject language where the variable is used";
    }

    @Override
    protected void addInjectionPlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project) {
        PsiElement psiElement = host.getParent();

        if (!(psiElement instanceof PsiNamedElement)) {
            psiElement = PsiTreeUtil.getChildOfAnyType(psiElement, PsiNamedElement.class);
        }

        if (psiElement == null) {
            addInjectionPlace(host, languageID, psiFile, project, new SingleInjectionStrategy());
            return;
        }

        addInjectionPlace(host, languageID, psiFile, project, new RootMultipleInjectionStrategy());
    }
}
