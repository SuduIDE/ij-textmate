package org.rri.ijTextmate.Helpers.InjectionHelper;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Inject.AbstractInjectLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.RootMultipleInjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.SingleInjectionStrategy;

public class JavaInjectionHelper implements InjectionHelper {
    private final static String FILE_EXTENSION = "java";

    @Override
    public boolean check(String fileExtension) {
        return FILE_EXTENSION.equalsIgnoreCase(fileExtension);
    }

    @Override
    public boolean availableMultiplePlaceInjection(Editor editor, PsiFile file) {
        PsiElement psiElement = InjectorHelper.findInjectionHost(editor, file);
        if (psiElement != null) psiElement = psiElement.getParent();

        return psiElement instanceof PsiNameIdentifierOwner;
    }

    @Override
    public void injectOnePlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project, @NotNull AbstractInjectLanguage injectLanguage) {
        DefaultInjectionHelper.INSTANCE.injectOnePlace(host, languageID, psiFile, project, injectLanguage);
    }

    @Override
    public void injectMultiplePlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project, AbstractInjectLanguage injectLanguage) {
        PsiElement psiElement = host.getParent();

        if (!(psiElement instanceof PsiNamedElement)) {
            psiElement = PsiTreeUtil.getChildOfAnyType(psiElement, PsiNamedElement.class);
        }

        if (psiElement == null) {
            injectLanguage.addInjectionPlace(host, languageID, psiFile, project, new SingleInjectionStrategy());
            return;
        }

        injectLanguage.addInjectionPlace(host, languageID, psiFile, project, new RootMultipleInjectionStrategy());
    }
}
