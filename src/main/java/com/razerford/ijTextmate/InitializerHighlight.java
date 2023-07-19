package com.razerford.ijTextmate;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import com.razerford.ijTextmate.Inject.InjectLanguage;
import com.razerford.ijTextmate.PersistentStorage.PersistentStorage;
import com.razerford.ijTextmate.PersistentStorage.TemporaryPlace;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.NotNull;

public class InitializerHighlight implements FileEditorManagerListener {
    private final Project project;

    public InitializerHighlight(Project project) {
        this.project = project;
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        PersistentStorage persistentStorage = PersistentStorage.getInstance(project);
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (persistentStorage == null || psiFile == null) return;
        for (TemporaryPlace p : persistentStorage.getState().getElements()) {
            PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(p.offset, psiFile);
            if (host != null && host.isValidHost()) {
                host.putUserData(InjectLanguage.MY_TEMPORARY_INJECTED_LANGUAGE, InjectedLanguage.create("textmate", p.languageId, p.languageId, false));
            }
        }
    }
}
