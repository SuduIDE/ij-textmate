package org.rri.ijTextmate.Helpers.InjectionHelper;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Inject.AbstractInjectLanguage;

public interface InjectionHelper {
    ExtensionPointName<InjectionHelper> EP_INJECTION_HELPER = ExtensionPointName.create("org.rri.ijTextmate.injectionHelper");
    DefaultInjectionHelper INSTANCE = new DefaultInjectionHelper();

    boolean check(String fileExtension);

    boolean availableMultiplePlaceInjection(Editor editor, PsiFile psiFile);

    void injectOnePlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project, AbstractInjectLanguage injectLanguage);

    void injectMultiplePlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project, AbstractInjectLanguage injectLanguage);

    static InjectionHelper getInjectionHelper(final String fileExtension) {
        for (var support : EP_INJECTION_HELPER.getExtensionList()) {
            if (support.check(fileExtension)) return support;
        }
        return INSTANCE;
    }
}
