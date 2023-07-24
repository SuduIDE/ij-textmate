package org.rri.ijTextmate.UnInject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;

public class UnInjectLanguage {
    public static void unInject(@NotNull PsiLanguageInjectionHost host, PlaceInjection placeInjection, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> removeInjectionPlace(host, placeInjection, project));
    }

    private static void removeInjectionPlace(PsiLanguageInjectionHost host, PlaceInjection placeInjection, Project project) {
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(project).getState();
        elements.remove(placeInjection);
        InjectorHelper.resolveInjectLanguage(host, null);
    }
}
