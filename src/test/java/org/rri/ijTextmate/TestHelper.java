package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import junit.framework.TestCase;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Inject.InjectLanguageMain;

import java.util.function.Consumer;

public class TestHelper {
    public static final String INJECTED_LANGUAGE = "sql";

    @FunctionalInterface
    public interface Assert {
        void test(boolean b);
    }

    @Contract(pure = true)
    public static @NotNull Assert createAssertTrueWithMessage(String message) {
        return (boolean b) -> TestCase.assertTrue(message, b);
    }

    @Contract(pure = true)
    public static @NotNull Assert createAssertFalseWithMessage(String message) {
        return (boolean b) -> TestCase.assertFalse(message, b);
    }

    public static void injectLanguage(Project project, Editor editor, PsiFile psiFile) {
        PsiLanguageInjectionHost host = TestHelper.getHost(editor, psiFile);
        if (host == null) return;
        InjectLanguageAction.injectLanguage(project, editor, psiFile, INJECTED_LANGUAGE, InjectLanguageMain.INSTANCE);
    }

    public static boolean isInjected(Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        psiFile.getManager().dropPsiCaches();
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, editor.getCaretModel().getOffset());
        return element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());
    }

    public static PsiLanguageInjectionHost getHost(Editor editor, PsiFile psiFile) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);
        host = InjectorHelper.resolveHost(host);
        return host;
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
