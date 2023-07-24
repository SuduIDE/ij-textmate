package org.rri.ijTextmate.Inject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.PersistentStorage.LanguageID;

public class InjectLanguage {
    public static void inject(@NotNull PsiLanguageInjectionHost host, @NotNull LanguageID languageID, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> addInjectionPlace(languageID, host));
    }

    private static void addInjectionPlace(LanguageID languageID, PsiLanguageInjectionHost host) {
        InjectorHelper.resolveInjectLanguage(host, languageID);
    }
}
