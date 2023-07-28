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
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

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
                PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                if (psiFile == null || psiFile.getUserData(Constants.MY_LANGUAGE_INJECTED) != null) continue;

                String relativePath = InjectorHelper.gitRelativePath(project, psiFile).toString();
                if (!TemporaryStorage.getInstance(project).contains(relativePath)) continue;

                TemporaryMapPointerToLanguage mapPointerToLanguage = TemporaryStorage.getInstance(project).get(relativePath);

                for (Map.Entry<SmartPsiElementPointer<PsiLanguageInjectionHost>, String> entry : mapPointerToLanguage.getMap().entrySet()) {
                    SmartPsiElementPointer<PsiLanguageInjectionHost> smartPsiElementPointer = entry.getKey();
                    String language = entry.getValue();
                    PsiElement psiElement = smartPsiElementPointer.getElement();
                    if (psiElement == null) continue;
                    TemporaryPlaceInjection temporaryPlaceInjection = new TemporaryPlaceInjection(smartPsiElementPointer, language);
                    psiElement.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, temporaryPlaceInjection);
                }

                psiFile.putUserData(Constants.MY_LANGUAGE_INJECTED, new Object());
            }
        }
    }
}
