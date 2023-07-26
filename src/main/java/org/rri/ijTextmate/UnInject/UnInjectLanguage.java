package org.rri.ijTextmate.UnInject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.PersistentStorage.SetElement;

public class UnInjectLanguage {
    public static void unInject(@NotNull PsiLanguageInjectionHost host, PlaceInjection placeInjection, PsiFile psiFile, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> removeInjectionPlace(host, placeInjection, psiFile, project));
    }

    private static void removeInjectionPlace(PsiLanguageInjectionHost host, @NotNull PlaceInjection placeInjection, PsiFile psiFile, Project project) {
        String relativePath = InjectorHelper.gitRelativePath(project, psiFile).toString();
        SetElement elements = PersistentStorage.getInstance(project).getState().get(relativePath);
        elements.remove(placeInjection.getCenter());
        InjectorHelper.resolveInjectLanguage(host, null);
    }
}
