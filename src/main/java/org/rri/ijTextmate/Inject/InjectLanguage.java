package org.rri.ijTextmate.Inject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.Storage.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.Storage.PersistentStorage.SetElement;

public class InjectLanguage {
    public static void inject(@NotNull PsiLanguageInjectionHost host, @NotNull PlaceInjection placeInjection, PsiFile psiFile, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> addInjectionPlace(placeInjection, host, psiFile, project));
    }

    private static void addInjectionPlace(PlaceInjection placeInjection, PsiLanguageInjectionHost host, PsiFile psiFile, Project project) {
        String relativePath = InjectorHelper.gitRelativePath(project, psiFile).toString();
        SetElement elements = PersistentStorage.getInstance(project).getState().get(relativePath);
        elements.add(placeInjection);
        InjectorHelper.resolveInjectLanguage(host, placeInjection);
    }
}
