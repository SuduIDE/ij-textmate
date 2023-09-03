package org.rri.ijTextmate.Inject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectionHelper.InjectionHelper;

public class InjectLanguageMultiplePlace extends AbstractInjectLanguage {
    public static InjectLanguageMultiplePlace INSTANCE = new InjectLanguageMultiplePlace();

    public String getIdentifier() {
        return "Inject language where the variable is used";
    }

    @Override
    final public void addInjectionPlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project, @NotNull InjectionHelper helper) {
        helper.injectMultiplePlace(host, languageID, psiFile, project, this);
    }
}
