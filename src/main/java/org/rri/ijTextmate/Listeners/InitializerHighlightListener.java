package org.rri.ijTextmate.Listeners;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentPlaceInjection;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.InjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.InjectionStrategyFactory;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

public class InitializerHighlightListener implements FileEditorManagerListener {
    private final Project project;

    public InitializerHighlightListener(Project project) {
        this.project = project;
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        PersistentStorage persistentStorage = PersistentStorage.getInstance(project);
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

        if (persistentStorage == null || psiFile == null) return;

        String relativePath = InjectorHelper.getRelativePath(project, psiFile);

        TemporaryMapPointerToPlaceInjection temporaryMapPointerToPlaceInjection = TemporaryStorage.getInstance(project).get(relativePath);

        for (PersistentPlaceInjection persistentPlaceInjection : persistentStorage.getSetPlaceInjectionAndClear(relativePath)) {
            PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(persistentPlaceInjection.getCenter(), psiFile);

            if (host != null && host.isValidHost()) {
                SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer = SmartPointerManager.createPointer(host);

                InjectionStrategy injectionStrategy = InjectionStrategyFactory.create(persistentPlaceInjection.identifierStrategy);

                TemporaryPlaceInjection temporaryPlaceInjection = new TemporaryPlaceInjection(psiElementPointer, persistentPlaceInjection.languageId, injectionStrategy);
                temporaryMapPointerToPlaceInjection.put(temporaryPlaceInjection);

                host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, temporaryPlaceInjection);
                host.getManager().dropPsiCaches();
            }

            psiFile.putUserData(Constants.MY_LANGUAGE_INJECTED, new Object());
        }
    }
}
