package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import junit.framework.TestCase;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Inject.InjectLanguage;

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
        InjectLanguage.inject(host, InjectedLanguage.create(INJECTED_LANGUAGE), project);
    }

    public static PsiLanguageInjectionHost getHost(Editor editor, PsiFile psiFile) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);
        host = InjectorHelper.resolveHost(host);
        return host;
    }
}
