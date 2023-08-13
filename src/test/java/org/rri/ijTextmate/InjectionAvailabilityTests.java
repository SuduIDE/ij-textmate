package org.rri.ijTextmate;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;
import org.rri.ijTextmate.Helpers.InjectorHelper;

public class InjectionAvailabilityTests extends LightPlatformCodeInsightFixture4TestCase {
    private static final int CENTER_INSIDE_OFFSET = 131;
    private static final int LEFT_INSIDE_OFFSET = 104;
    private static final int RIGHT_INSIDE_OFFSET = 158;
    private static final int LEFT_OUTSIDE_OFFSET = 99;
    private static final int RIGHT_OUTSIDE_OFFSET = 160;
    private static final String JAVA_FILE = "InjectionAvailabilityTestCase.java";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void testInjectionAvailabilityJava() {
        PsiFile psiFile = myFixture.configureByFile(JAVA_FILE);
        Project project = myFixture.getProject();
        Editor editor = myFixture.getEditor();

        editor.getCaretModel().moveToOffset(CENTER_INSIDE_OFFSET);
        assertTrue(getMessageIfLanguageCanBeInjected(), canInjectLanguageToHost(project, psiFile, editor));

        editor.getCaretModel().moveToOffset(LEFT_INSIDE_OFFSET);
        assertTrue(getMessageIfLanguageCanBeInjected(), canInjectLanguageToHost(project, psiFile, editor));

        editor.getCaretModel().moveToOffset(RIGHT_INSIDE_OFFSET);
        assertTrue(getMessageIfLanguageCanBeInjected(), canInjectLanguageToHost(project, psiFile, editor));

        editor.getCaretModel().moveToOffset(LEFT_OUTSIDE_OFFSET);
        assertFalse(getMessageIfLanguageCanNotBeInjected(), canInjectLanguageToHost(project, psiFile, editor));

        editor.getCaretModel().moveToOffset(RIGHT_OUTSIDE_OFFSET);
        assertFalse(getMessageIfLanguageCanNotBeInjected(), canInjectLanguageToHost(project, psiFile, editor));
    }

    private boolean canInjectLanguageToHost(Project project, PsiFile psiFile, Editor editor) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);

        InjectLanguageAction action = new InjectLanguageAction();

        return action.canInjectLanguageToHost(project, editor, psiFile, host);
    }

    private String getMessageIfLanguageCanNotBeInjected() {
        return String.format("\nMessage: %s\n", "Language can not be injected in this literal. But the test says it can");
    }

    private String getMessageIfLanguageCanBeInjected() {
        return String.format("\nMessage: %s\n", "Language can be injected in this literal. But the test says it can't");
    }
}
