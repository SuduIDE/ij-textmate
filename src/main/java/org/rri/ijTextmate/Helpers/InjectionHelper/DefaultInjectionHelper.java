package org.rri.ijTextmate.Helpers.InjectionHelper;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Inject.AbstractInjectLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.SingleInjectionStrategy;

public class DefaultInjectionHelper implements InjectionHelper {
    private static final Logger LOG = Logger.getInstance(DefaultInjectionHelper.class);

    @Override
    public boolean check(String fileExtension) {
        return true;
    }

    @Override
    public boolean availableMultiplePlaceInjection(Editor editor, PsiFile psiFile) {
        return false;
    }

    @Override
    public void injectOnePlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project, @NotNull AbstractInjectLanguage injectLanguage) {
        injectLanguage.addInjectionPlace(host, languageID, psiFile, project, new SingleInjectionStrategy());
    }

    @Override
    public void injectMultiplePlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project, AbstractInjectLanguage injectLanguage) {
        LOG.error(String.format("Injection into variable usage locations is not supported: %s", DefaultInjectionHelper.class.getName()));
    }
}
