package com.razerford.ijTextmate;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.util.FileContentUtil;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import com.razerford.ijTextmate.UnInject.UnInjectLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class UnInjectLanguageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null && editor != null && file != null;
        unInjectLanguage(project, editor, file);
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
        if (host == null || host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE) != null) {
            e.getPresentation().setEnabledAndVisible(true);
            return;
        }
        e.getPresentation().setEnabledAndVisible(false);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    public static void unInjectLanguage(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);
        if (host == null) return;
        UnInjectLanguage.unInject(host, project);
//      Todo: сделать удаление инъекции из PersistentStorage
//        PersistentStorage.SetElement elements = project.getService(PersistentStorage.class).getState();
//        elements.remove();
        FileContentUtil.reparseFiles(project, Collections.emptyList(), false);
    }
}
