package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectionHelper.InjectionHelper;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Inject.InjectLanguageOnePlace;

import java.util.function.Consumer;

public class TestHelper {
    public static final String INJECTED_LANGUAGE = "sql";

    public static void injectLanguage(Project project, Editor editor, PsiFile psiFile, String language) {
        PsiLanguageInjectionHost host = TestHelper.getHost(editor, psiFile);
        if (host == null) return;
        var data = new InjectLanguageAction.Data(project, editor, psiFile);
        InjectLanguageAction.injectLanguage(data, language, InjectLanguageOnePlace.INSTANCE, InjectionHelper.INSTANCE);
    }

    public static void injectLanguage(Project project, Editor editor, PsiFile psiFile) {
        injectLanguage(project, editor, psiFile, INJECTED_LANGUAGE);
    }

    public static boolean isInjected(Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        psiFile.getManager().dropPsiCaches();
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, editor.getCaretModel().getOffset());
        return element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());
    }

    public static PsiLanguageInjectionHost getHost(Editor editor, PsiFile psiFile) {
        return InjectorHelper.findInjectionHost(editor, psiFile);
    }

    @SuppressWarnings("unused")
    @Contract(pure = true)
    public static void checkWithConsumer(Consumer<Object> check, Object @NotNull ... args) {
        for (Object arg : args) {
            check.accept(arg);
        }
    }

    @SuppressWarnings("unused")
    public static String getMessage(String testName, String fileName, String message) {
        return String.format("\nName test: %s\nFile: %s\nMessage: %s", testName, fileName, message);
    }

    public static String getMessage(String fileName, String message) {
        return String.format("\nFile: %s\nMessage: %s", fileName, message);
    }
}
