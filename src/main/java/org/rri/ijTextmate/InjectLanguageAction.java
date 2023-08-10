package org.rri.ijTextmate;

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
import com.intellij.util.FileContentUtil;
import com.intellij.util.Processor;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Helpers.TextMateHelper;
import org.rri.ijTextmate.Inject.AbstractInjectLanguage;
import org.rri.ijTextmate.Inject.InjectLanguageMain;
import org.intellij.plugins.intelliLang.references.InjectedReferencesContributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Inject.InjectLanguageOnePlace;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class InjectLanguageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null && editor != null && file != null;
        chooseInjectionStrategy(project, editor, file);
//        chooseLanguageForInjection(project, editor, file, InjectLanguageMain.INSTANCE);
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
        if (!canInjectLanguageToHost(project, editor, file, host)) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        host = InjectorHelper.resolveHost(host);
        e.getPresentation().setEnabledAndVisible(canInjectLanguageToHost(project, editor, file, host));
    }

    public boolean canInjectLanguageToHost(Project project, Editor editor, PsiFile file, PsiLanguageInjectionHost host) {
        if (host == null || host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE) != null) {
            return false;
        }
        List<Pair<PsiElement, TextRange>> injectedPsi = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(host);
        if (injectedPsi == null || injectedPsi.isEmpty()) {
            return !InjectedReferencesContributor.isInjected(file.findReferenceAt(editor.getCaretModel().getOffset()));
        }
        return false;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    private void chooseInjectionStrategy(Project project, Editor editor, PsiFile file) {
        List<AbstractInjectLanguage> listStrategy = List.of(InjectLanguageMain.INSTANCE, InjectLanguageOnePlace.INSTANCE);

        ColoredListCellRenderer<AbstractInjectLanguage> listCellRenderer = new ColoredListCellRenderer<>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends AbstractInjectLanguage> list, AbstractInjectLanguage value, int index, boolean selected, boolean hasFocus) {
                append(value.getIdentifier());
            }
        };
        Processor<? super AbstractInjectLanguage> processor = new Processor<>() {
            @Override
            public boolean process(AbstractInjectLanguage injector) {
                chooseLanguageForInjection(project, editor, file, injector);
                return false;
            }
        };

        IPopupChooserBuilder<AbstractInjectLanguage> builder = JBPopupFactory.getInstance()
                .createPopupChooserBuilder(listStrategy).setRenderer(listCellRenderer)
                .setNamerForFiltering(AbstractInjectLanguage::getIdentifier).setItemChosenCallback(processor::process);
        builder.createPopup().showInBestPositionFor(editor);
    }

    private void chooseLanguageForInjection(Project project, Editor editor, PsiFile file, AbstractInjectLanguage injector) {
        TextMateHelper textMateHelper = TextMateHelper.upateLanguagesAndGetTextMateHelper(project);
        List<String> listLanguages = textMateHelper.getLanguages();

        ColoredListCellRenderer<String> listCellRenderer = createColoredListCellRenderer();
        Processor<? super String> processor = createProcessor(project, editor, file, injector);

        IPopupChooserBuilder<String> builder = JBPopupFactory.getInstance()
                .createPopupChooserBuilder(listLanguages).setRenderer(listCellRenderer)
                .setNamerForFiltering(x -> x).setItemChosenCallback(processor::process);
        builder.createPopup().showInBestPositionFor(editor);
    }

    public static void injectLanguage(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile, @NotNull String languageId, @NotNull AbstractInjectLanguage injector) {
        int offset = editor.getCaretModel().getOffset();
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(offset, psiFile);
        if (host == null) return;
        injector.inject(host, languageId, psiFile, project);
        FileContentUtil.reparseFiles(project, Collections.emptyList(), false);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    private static @NotNull Processor<? super String> createProcessor(Project project, Editor editor, PsiFile file, AbstractInjectLanguage injector) {
        return new Processor<>() {
            @Override
            public boolean process(String language) {
                injectLanguage(project, editor, file, language, injector);
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
