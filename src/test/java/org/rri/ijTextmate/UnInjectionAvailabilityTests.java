package org.rri.ijTextmate;

import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Objects;

public class UnInjectionAvailabilityTests extends LightPlatformCodeInsightFixture4TestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        checkUnInjectionAvailability("CaretInTheCenterInsideTheString.java", caretInsideString);
    }

    @Test
    public void testCaretOnTheLeftInsideTheString() {
        checkUnInjectionAvailability("CaretOnTheLeftInsideTheString.java", caretInsideString);
    }

    @Test
    public void testCaretOnTheRightInsideTheString() {
        checkUnInjectionAvailability("CaretOnTheRightInsideTheString.java", caretInsideString);
    }

    @Test
    public void testCaretOnTheLeftOutsideOfTheString() {
        checkUnInjectionAvailability("CaretOnTheLeftOutsideOfTheString.java", caretOutsideString);
    }

    @Test
    public void testCaretOnTheRightOutsideOfTheString() {
        checkUnInjectionAvailability("CaretOnTheRightOutsideOfTheString.java", caretOutsideString);
    }

    @Test
    public void testSelectionInsideTheString() {
        PsiFile psiFile = myFixture.configureByFile("SelectionInsideTheString.java");
        Editor editor = myFixture.getEditor();
        Project project = myFixture.getProject();

        for (CaretState caretState : editor.getCaretModel().getCaretsAndSelections()) {
            TestHelper.checkWithConsumer(TestCase::assertNotNull, caretState.getSelectionStart(), caretState.getSelectionEnd());

            int i = Objects.requireNonNull(caretState.getSelectionStart()).column;
            int end = Objects.requireNonNull(caretState.getSelectionEnd()).column;
            int line = caretState.getSelectionStart().line;

            do {
                editor.getCaretModel().moveToVisualPosition(new VisualPosition(line, i));
                TestHelper.checkWithConsumer(TestCase::assertNotNull, project, psiFile, editor);
                caretInsideString.check(project, psiFile, editor);
            } while (++i < end);
        }
    }

    private void checkUnInjectionAvailability(final String fileName, @NotNull CheckWithInjectedLanguage checking) {
        Project project = myFixture.getProject();
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = myFixture.getEditor();

        checking.check(project, psiFile, editor);
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
