package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.util.FileContentUtil;
import junit.framework.TestCase;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Inject.InjectLanguage;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.TemporaryPlace;

import java.util.Collections;
import java.util.function.Consumer;

public class TestHelper {
    private static final String INJECTED_LANGUAGE = "sql";
    private static final MultiHostInjector INJECTOR = new LanguageHighlight();

    public static final Assert ASSERT_FALSE = TestCase::assertFalse;
    public static final Assert ASSERT_TRUE = TestCase::assertTrue;

    @FunctionalInterface
    public interface Assert {
        void test(boolean b);
    }

    public static void injectLanguage(Project project, Editor editor, PsiFile psiFile, Disposable disposable) {
        PsiLanguageInjectionHost host = TestHelper.getHost(editor, psiFile);
        if (host == null) return;
        InjectedLanguageManager.getInstance(project).registerMultiHostInjector(INJECTOR, disposable);
        InjectLanguageAction.injectLanguage(project, editor, psiFile, INJECTED_LANGUAGE);
    }

    public static PsiLanguageInjectionHost getHost(Editor editor, PsiFile psiFile) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);
        host = InjectorHelper.resolveHost(host);
        return host;
    }

    @Contract(pure = true)
    public static void checkWithConsumer(Consumer<Object> check, Object @NotNull ... args) {
        for (Object arg : args) {
            check.accept(arg);
        }
    }
}
