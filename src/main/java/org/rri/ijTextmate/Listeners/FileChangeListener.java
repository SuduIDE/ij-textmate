package org.rri.ijTextmate.Listeners;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FileChangeListener implements BulkFileListener {
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent vfe : events) {

            VirtualFile vf = vfe.getFile();
            if (vf == null) continue;

            Collection<Project> projects = ProjectLocator.getInstance().getProjectsForFile(vf);

            for (Project project : projects) {
                addInjectedLanguageIntoProjectFiles(project, vf);
            }
        }
    }

    void addInjectedLanguageIntoProjectFiles(Project project, VirtualFile vf) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
        if (psiFile == null || psiFile.getUserData(Constants.MY_LANGUAGE_INJECTED) != null) return;

        String relativePath = InjectorHelper.getRelativePath(project, psiFile);
        if (!TemporaryStorage.getInstance(project).contains(relativePath)) return;

        TemporaryMapPointerToPlaceInjection mapPointerToPlaceInjection = TemporaryStorage.getInstance(project).get(relativePath);

        insertInjectedLanguageIntoFileStringLiterals(mapPointerToPlaceInjection);

        psiFile.putUserData(Constants.MY_LANGUAGE_INJECTED, new Object());
    }

    void insertInjectedLanguageIntoFileStringLiterals(@NotNull TemporaryMapPointerToPlaceInjection mapPointerToPlaceInjection) {
        List<SmartPsiElementPointer<PsiLanguageInjectionHost>> removed = new ArrayList<>();
        for (Map.Entry<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection> entry : mapPointerToPlaceInjection.getMap().entrySet()) {
            SmartPsiElementPointer<PsiLanguageInjectionHost> smartPsiElementPointer = entry.getKey();

            PsiElement psiElement = smartPsiElementPointer.getElement();
            if (psiElement == null) {
                removed.add(smartPsiElementPointer);
                continue;
            }

            psiElement.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, entry.getValue());
        }
        for (var key : removed) {
            mapPointerToPlaceInjection.remove(key);
        }
    }
}
