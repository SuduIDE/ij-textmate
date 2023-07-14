package com.razerford.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import com.razerford.ijTextmate.TemporaryLanguage.LanguageInjectionSupport;
import org.intellij.plugins.intelliLang.references.InjectedReferencesContributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InjectLanguageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("Hello world");
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
//        FileTypeManager fileTypeManager = FileTypeManager.getInstance();
        assert project != null && editor != null && file != null;
//        org.intellij.plugins.intelliLang.inject.InjectLanguageAction.invokeImpl(project, editor, file, InjectableTextMate.INSTANCE);
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        assert element != null;
        System.out.println(element.getClass());
        System.out.println(element.getText());
        System.out.println(element.getContext());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Project project = e.getData(CommonDataKeys.PROJECT);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (file == null || editor == null || project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, file);
        if (host == null || host.getUserData(LanguageInjectionSupport.KEY_TEMPORARY_INJECTED_LANGUAGE) != null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        List<Pair<PsiElement, TextRange>> injectedPsi = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(host);
        if (injectedPsi == null || injectedPsi.isEmpty()) {
            e.getPresentation().setEnabledAndVisible(!InjectedReferencesContributor.isInjected(file.findReferenceAt(editor.getCaretModel().getOffset())));
            return;
        }
        e.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
