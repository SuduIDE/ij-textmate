package com.razerford.ijTextmate;

import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import com.razerford.ijTextmate.TemporaryEntity.TemporaryLanguageInjectionSupport;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.intellij.plugins.intelliLang.references.InjectedReferencesContributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InjectLanguageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null && editor != null && file != null;
        actionPerformedImpl(project, editor, file, "");
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
        if (host == null || host.getUserData(TemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE) != null) {
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

    public static void actionPerformedImpl(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile, @NotNull String languageId) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);
        if (host == null) return;

//        Language language = InjectableTextMate.create(languageId);
////        project.getService(MyTemporaryLanguageInjection.class).addInjectionInPlace(language, host);
//        MyTemporaryPlacesRegistry.getInstance(project).addHostWithUndo(project, host, InjectedLanguage.create(language.getID()));
//        try {
//        } catch (Throwable ignore) {
//        }
//        host.putUserData(MyTemporaryLanguageInjectionImpl.MY_TEMPORARY_LANGUAGE_INJECTION, InjectedLanguage.create(language.getID()));
//        FileContentUtil.reparseFiles(project, Collections.emptyList(), true);
    }
}
