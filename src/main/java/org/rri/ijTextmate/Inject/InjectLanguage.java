package org.rri.ijTextmate.Inject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;

public class InjectLanguage {
    public static void inject(@NotNull PsiLanguageInjectionHost host, @NotNull PlaceInjection placeInjection, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> addInjectionPlace(placeInjection, host, project));
    }

    private static void addInjectionPlace(PlaceInjection placeInjection, PsiLanguageInjectionHost host, Project project) {
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(project).getState();
        elements.add(placeInjection);
        InjectorHelper.resolveInjectLanguage(host, placeInjection);
    }
}
