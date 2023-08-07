package org.rri.ijTextmate;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;

public class UnInjectionAvailabilityTests extends LightPlatformCodeInsightFixture4TestCase {
    private static final int CENTER_INSIDE_OFFSET = 131;
    private static final int LEFT_INSIDE_OFFSET = 104;
    private static final int RIGHT_INSIDE_OFFSET = 158;
    private static final int LEFT_OUTSIDE_OFFSET = 99;
    private static final int RIGHT_OUTSIDE_OFFSET = 160;
    private static final String JAVA_FILE = "UnInjectionAvailabilityTestCase.java";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        Project project = myFixture.getProject();
        PsiFile psiFile = myFixture.configureByFile(JAVA_FILE);
        Editor editor = myFixture.getEditor();

        editor.getCaretModel().moveToOffset(CENTER_INSIDE_OFFSET);
        caretInsideString.check(project, psiFile, editor);

        editor.getCaretModel().moveToOffset(LEFT_INSIDE_OFFSET);
        caretInsideString.check(project, psiFile, editor);

        editor.getCaretModel().moveToOffset(RIGHT_INSIDE_OFFSET);
        caretInsideString.check(project, psiFile, editor);


        editor.getCaretModel().moveToOffset(LEFT_OUTSIDE_OFFSET);
        caretOutsideString.check(project, psiFile, editor);

        editor.getCaretModel().moveToOffset(RIGHT_OUTSIDE_OFFSET);
        caretOutsideString.check(project, psiFile, editor);
    }

    private boolean canUnInjectLanguageToHost(Project project, PsiFile psiFile, Editor editor) {
        PsiLanguageInjectionHost host = TestHelper.getHost(editor, psiFile);
        UnInjectLanguageAction action = new UnInjectLanguageAction();
        return action.canUnInjectLanguageToHost(project, editor, psiFile, host);
    }

    private final CheckWithInjectedLanguage caretInsideString = (Project project, PsiFile psiFile, Editor editor) -> {
        TestHelper.injectLanguage(project, editor, psiFile);
        String message = String.format("\nFile: %s\nMessage: you can to inject language into a literal\n", psiFile.getName());
        assertTrue(message, canUnInjectLanguageToHost(project, psiFile, editor));
    };

    private final CheckWithInjectedLanguage caretOutsideString = (Project project, PsiFile psiFile, Editor editor) -> {
        String message = String.format("\nFile: %s\nMessage: you can't to inject language into a literal\n", psiFile.getName());
        assertFalse(message, canUnInjectLanguageToHost(project, psiFile, editor));
    };

    @FunctionalInterface
    private interface CheckWithInjectedLanguage {
        void check(Project project, PsiFile psiFile, Editor editor);
    }
}
