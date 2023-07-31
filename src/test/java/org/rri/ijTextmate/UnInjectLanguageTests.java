package org.rri.ijTextmate;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;

public class UnInjectLanguageTests extends LightPlatformCodeInsightFixture4TestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        checkUnInjectLanguage("CaretInTheCenterInsideTheString.java");
    }

    @Test
    public void testCaretOnTheLeftInsideTheString() {
        checkUnInjectLanguage("CaretOnTheLeftInsideTheString.java");
    }

    @Test
    public void testCaretOnTheRightInsideTheString() {
        checkUnInjectLanguage("CaretOnTheRightInsideTheString.java");
    }

    private void checkUnInjectLanguage(String fileName) {
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Project project = myFixture.getProject();
        Editor editor = myFixture.getEditor();

        TestHelper.injectLanguage(project, editor, psiFile);

        assertTrue(TestHelper.isInjected(project, editor, psiFile));

        UnInjectLanguageAction.unInjectLanguage(project, editor, psiFile);

        String message = String.format("\nFile: %s\nMessage: the literal must not contain an injection after deletion\n", fileName);
        assertFalse(message, TestHelper.isInjected(project, editor, psiFile));
    }
}
