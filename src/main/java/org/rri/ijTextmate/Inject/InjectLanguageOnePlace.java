package org.rri.ijTextmate.Inject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectionHelper.InjectionHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.SingleInjectionStrategy;

public class InjectLanguageOnePlace extends AbstractInjectLanguage {
    public static InjectLanguageOnePlace INSTANCE = new InjectLanguageOnePlace();

    public String getIdentifier() {
        return "Inject language in the current place";
    }

    @Override
    final public void addInjectionPlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project, InjectionHelper helper) {
        addInjectionPlace(host, languageID, psiFile, project, new SingleInjectionStrategy());
    }
}
