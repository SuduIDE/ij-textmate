package com.razerford.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IPopupChooserBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.util.Processor;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import com.razerford.ijTextmate.Helpers.TextMateHelper;
import com.razerford.ijTextmate.Inject.InjectLanguage;
import com.razerford.ijTextmate.TemporaryEntity.MyTemporaryLanguageInjectionSupport;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.intellij.plugins.intelliLang.references.InjectedReferencesContributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class InjectLanguageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null && editor != null && file != null;
        TextMateHelper textMateHelper = project.getService(TextMateHelper.class);
        textMateHelper.updateLanguages();
        List<String> listLanguages = textMateHelper.getLanguages();
        ColoredListCellRenderer<String> listCellRenderer = createColoredListCellRenderer();
        Processor<? super String> processor = createProcessor(project, editor, file);
        IPopupChooserBuilder<String> builder = JBPopupFactory.getInstance()
                .createPopupChooserBuilder(listLanguages).setRenderer(listCellRenderer)
                .setNamerForFiltering(x -> x).setItemChosenCallback(processor::process);
        builder.createPopup().showInBestPositionFor(editor);
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
        if (host == null || host.getUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE) != null) {
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
        InjectedLanguage injectedLanguage = InjectableTextMate.create(languageId);
        InjectLanguage.inject(host, injectedLanguage, project);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    private static @NotNull Processor<? super String> createProcessor(Project project, Editor editor, PsiFile file) {
        return new Processor<>() {
            @Override
            public boolean process(String language) {
                actionPerformedImpl(project, editor, file, language);
                return false;
            }
        };
    }

    @Contract(" -> new")
    private static @NotNull ColoredListCellRenderer<String> createColoredListCellRenderer() {
        return new ColoredListCellRenderer<>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends String> list, String language, int index, boolean selected, boolean hasFocus) {
                append(language);
            }
        };
    }
}
