package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Inject.InjectLanguage;

import static org.rri.ijTextmate.TestHelper.ASSERT_FALSE;
import static org.rri.ijTextmate.TestHelper.ASSERT_TRUE;

public class InjectTests extends LightJavaCodeInsightFixtureTestCase {
    private static final String INJECTED_LANGUAGE = "sql";
    private static final MultiHostInjector INJECTOR = new LanguageHighlight();

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionAvailabilityCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        checkInject("CaretInTheCenterInsideTheString.java", ASSERT_TRUE);
        checkInject("CaretInTheCenterInsideTheString.py", ASSERT_TRUE);
        checkInject("CaretInTheCenterInsideTheString.json", ASSERT_TRUE);
    }

    @Test
    public void testCaretOnTheLeftInsideTheString() {
        checkInject("CaretOnTheLeftInsideTheString.java", ASSERT_TRUE);
        checkInject("CaretOnTheLeftInsideTheString.py", ASSERT_TRUE);
        checkInject("CaretOnTheLeftInsideTheString.json", ASSERT_TRUE);
    }

    @Test
    public void testCaretOnTheRightInsideTheString() {
        checkInject("CaretOnTheRightInsideTheString.java", ASSERT_TRUE);
        checkInject("CaretOnTheRightInsideTheString.py", ASSERT_TRUE);
        checkInject("CaretOnTheRightInsideTheString.json", ASSERT_TRUE);
    }

    @Test
    public void testCaretOnTheLeftOutsideOfTheString() {
        checkInject("CaretOnTheLeftOutsideOfTheString.java", ASSERT_FALSE);
        checkInject("CaretOnTheLeftOutsideOfTheString.py", ASSERT_FALSE);
        checkInject("CaretOnTheLeftOutsideOfTheString.json", ASSERT_FALSE);
    }

    @Test
    public void testCaretOnTheRightOutsideOfTheString() {
        checkInject("CaretOnTheRightOutsideOfTheString.java", ASSERT_FALSE);
        checkInject("CaretOnTheRightOutsideOfTheString.py", ASSERT_FALSE);
        checkInject("CaretOnTheRightOutsideOfTheString.json", ASSERT_FALSE);
    }


    private void checkInject(String fileName, TestHelper.@NotNull Assert test) {
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Project project = getProject();
        Editor editor = getEditor();
        injectLanguage(project, editor, psiFile);
        test.test(isInjected(project, editor, psiFile));
    }

    private void injectLanguage(Project project, Editor editor, PsiFile psiFile) {
        PsiLanguageInjectionHost host = getHost(editor, psiFile);
        if (host == null) return;
        InjectedLanguageManager.getInstance(project).registerMultiHostInjector(INJECTOR, getTestRootDisposable());
        InjectLanguage.inject(host, InjectedLanguage.create(INJECTED_LANGUAGE), project);
    }

    private PsiLanguageInjectionHost getHost(Editor editor, PsiFile psiFile) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);
        host = InjectorHelper.resolveHost(host);
        return host;
    }

    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, editor.getCaretModel().getOffset());
        return element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());
    }
}
