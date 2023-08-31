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
import org.rri.ijTextmate.Helpers.InjectionHelper.InjectionHelper;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Helpers.TextMateHelper;
import org.rri.ijTextmate.Inject.AbstractInjectLanguage;
import org.rri.ijTextmate.Inject.InjectLanguageMultiplePlace;
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
        Data data = new Data(project, editor, file);
        chooseInjection(data);
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

    private void chooseInjection(@NotNull Data data) {
        InjectionHelper helper = InjectionHelper.getInjectionHelper(data.file.getFileType().getName());
        if (helper.availableMultiplePlaceInjection(data.editor, data.file)) {
            chooseInjectionStrategy(data, helper);
        } else {
            chooseLanguageForInjection(data, InjectLanguageOnePlace.INSTANCE, helper);
        }
    }

    private void chooseInjectionStrategy(@NotNull Data data, @NotNull InjectionHelper helper) {
        List<AbstractInjectLanguage> listStrategy = List.of(InjectLanguageOnePlace.INSTANCE, InjectLanguageMultiplePlace.INSTANCE);

        ColoredListCellRenderer<AbstractInjectLanguage> listCellRenderer = ColoredListFactory.createListAbstractInjectLanguage();

        Processor<? super AbstractInjectLanguage> processor = ProcessFactory.createProcessor(data, this, helper);

        IPopupChooserBuilder<AbstractInjectLanguage> builder = JBPopupFactory.getInstance().createPopupChooserBuilder(listStrategy).setRenderer(listCellRenderer).setNamerForFiltering(AbstractInjectLanguage::getIdentifier).setItemChosenCallback(processor::process);
        builder.createPopup().showInBestPositionFor(data.editor);
    }

    private void chooseLanguageForInjection(@NotNull Data data, AbstractInjectLanguage injector, InjectionHelper helper) {
        TextMateHelper textMateHelper = TextMateHelper.upateLanguagesAndGetTextMateHelper(data.project);
        List<String> listLanguages = textMateHelper.getLanguages();

        ColoredListCellRenderer<String> listCellRenderer = ColoredListFactory.createListString();
        Processor<? super String> processor = ProcessFactory.createProcessor(data, injector, helper);

        IPopupChooserBuilder<String> builder = JBPopupFactory.getInstance().createPopupChooserBuilder(listLanguages).setRenderer(listCellRenderer).setNamerForFiltering(x -> x).setItemChosenCallback(processor::process);
        builder.createPopup().showInBestPositionFor(data.editor);
    }

    public static void injectLanguage(@NotNull Data data, @NotNull String languageId, @NotNull AbstractInjectLanguage injector, InjectionHelper helper) {
        int offset = data.editor.getCaretModel().getOffset();
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(offset, data.file);
        if (host == null) return;
        injector.inject(host, languageId, data.file, data.project, helper);
        FileContentUtil.reparseFiles(data.project, Collections.emptyList(), false);
    }

    public static class ProcessFactory {
        private static @NotNull Processor<? super AbstractInjectLanguage> createProcessor(@NotNull Data data, InjectLanguageAction action, InjectionHelper helper) {
            return (Processor<AbstractInjectLanguage>) injector -> {
                action.chooseLanguageForInjection(data, injector, helper);
                return false;
            };
        }

        @Contract(value = "_, _, _ -> new", pure = true)
        private static @NotNull Processor<? super String> createProcessor(@NotNull Data data, AbstractInjectLanguage injector, InjectionHelper helper) {
            return (Processor<String>) language -> {
                injectLanguage(data, language, injector, helper);
                return false;
            };
        }

    }

    public static class ColoredListFactory {
        @Contract(" -> new")
        private static @NotNull ColoredListCellRenderer<String> createListString() {
            return new ColoredListCellRenderer<>() {
                @Override
                protected void customizeCellRenderer(@NotNull JList<? extends String> list, String language, int index, boolean selected, boolean hasFocus) {
                    append(language);
                }
            };
        }

        @Contract(" -> new")
        private static @NotNull ColoredListCellRenderer<AbstractInjectLanguage> createListAbstractInjectLanguage() {
            return new ColoredListCellRenderer<>() {
                @Override
                protected void customizeCellRenderer(@NotNull JList<? extends AbstractInjectLanguage> list, @NotNull AbstractInjectLanguage value, int index, boolean selected, boolean hasFocus) {
                    append(value.getIdentifier());
                }
            };
        }
    }

    public record Data(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
    }
}
