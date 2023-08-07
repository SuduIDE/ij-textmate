package org.rri.ijTextmate;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;

public class UnLanguageInjectorTests extends LightPlatformCodeInsightFixture4TestCase {
    private static final int CENTER_INSIDE_OFFSET = 131;
    private static final int LEFT_INSIDE_OFFSET = 105;
    private static final int RIGHT_INSIDE_OFFSET = 155;
    private static final String JAVA_FILE = "LanguageUnInjectionTestCase.java";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        checkUnInjectLanguage(CENTER_INSIDE_OFFSET);

        checkUnInjectLanguage(LEFT_INSIDE_OFFSET);

        checkUnInjectLanguage(RIGHT_INSIDE_OFFSET);
    }

    private void checkUnInjectLanguage(int offset) {
        PsiFile psiFile = myFixture.getFile();

        if (psiFile == null) {
            psiFile = myFixture.configureByFile(JAVA_FILE);
        }

        Project project = myFixture.getProject();
        Editor editor = myFixture.getEditor();

        editor.getCaretModel().moveToOffset(offset);

        TestHelper.injectLanguage(project, editor, psiFile);

        assertTrue(TestHelper.isInjected(project, editor, psiFile));

        UnInjectLanguageAction.unInjectLanguage(project, editor, psiFile);

        String message = String.format("\nFile: %s\nMessage: the literal must not contain an injection after deletion\n", "LanguageUnInjectionTestCase.java");
        assertFalse(message, TestHelper.isInjected(project, editor, psiFile));
    }
}
