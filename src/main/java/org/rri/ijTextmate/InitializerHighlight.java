package org.rri.ijTextmate;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.Storage.PersistentStorage.PlaceInjection;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

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

        String relativePath = InjectorHelper.gitRelativePath(project, psiFile).toString();

        for (PlaceInjection placeInjection : persistentStorage.getState().get(relativePath)) {
            PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(placeInjection.getCenter(), psiFile);
            host = InjectorHelper.resolveHost(host);

            if (host != null && host.isValidHost()) {
                SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer = SmartPointerManager.createPointer(host);
                TemporaryPlaceInjection temporaryPlaceInjection = new TemporaryPlaceInjection(psiElementPointer, placeInjection.languageId);
                host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, temporaryPlaceInjection);
                host.getManager().dropPsiCaches();
            }
        }
    }
}
