package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class UnInjectLanguageTests extends JavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionAvailabilityCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        checkUnInjectLanguage("CaretInTheCenterInsideTheString.java");
        checkUnInjectLanguage("CaretInTheCenterInsideTheString.py");
        checkUnInjectLanguage("CaretInTheCenterInsideTheString.json");
    }

    @Test
    public void testCaretOnTheLeftInsideTheString() {
        checkUnInjectLanguage("CaretOnTheLeftInsideTheString.java");
        checkUnInjectLanguage("CaretOnTheLeftInsideTheString.py");
        checkUnInjectLanguage("CaretOnTheLeftInsideTheString.json");
    }

    @Test
    public void testCaretOnTheRightInsideTheString() {
        checkUnInjectLanguage("CaretOnTheRightInsideTheString.java");
        checkUnInjectLanguage("CaretOnTheRightInsideTheString.py");
        checkUnInjectLanguage("CaretOnTheRightInsideTheString.json");
    }

    private void checkUnInjectLanguage(String fileName) {
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Project project = getProject();
        Editor editor = myFixture.getEditor();
        TestHelper.checkWithConsumer(TestCase::assertNotNull, project, psiFile, editor);
        TestHelper.injectLanguage(project, editor, psiFile, getTestRootDisposable());
        assertTrue(isInjected(project, editor, psiFile));
        UnInjectLanguageAction.unInjectLanguage(project, editor, psiFile);
        assertFalse(isInjected(project, editor, psiFile));
    }


    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        PsiManager.getInstance(project).dropPsiCaches();
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, editor.getCaretModel().getOffset());
        return element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());
    }
}
