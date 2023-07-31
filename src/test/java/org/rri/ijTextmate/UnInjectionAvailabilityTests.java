package org.rri.ijTextmate;

import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UnInjectionAvailabilityTests extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    public void testCaretInTheCenterInsideTheString() {
        checkUnInjectionAvailability("CaretInTheCenterInsideTheString.java", caretInsideString);
        checkUnInjectionAvailability("CaretInTheCenterInsideTheString.py", caretInsideString);
    }

    public void testCaretOnTheLeftInsideTheString() {
        checkUnInjectionAvailability("CaretOnTheLeftInsideTheString.java", caretInsideString);
        checkUnInjectionAvailability("CaretOnTheLeftInsideTheString.py", caretInsideString);
    }

    public void testCaretOnTheRightInsideTheString() {
        checkUnInjectionAvailability("CaretOnTheRightInsideTheString.java", caretInsideString);
        checkUnInjectionAvailability("CaretOnTheRightInsideTheString.py", caretInsideString);
    }

    public void testCaretOnTheLeftOutsideOfTheString() {
        checkUnInjectionAvailability("CaretOnTheLeftOutsideOfTheString.java", caretOutsideString);
        checkUnInjectionAvailability("CaretOnTheLeftOutsideOfTheString.py", caretOutsideString);
    }

    public void testCaretOnTheRightOutsideOfTheString() {
        checkUnInjectionAvailability("CaretOnTheRightOutsideOfTheString.java", caretOutsideString);
        checkUnInjectionAvailability("CaretOnTheRightOutsideOfTheString.py", caretOutsideString);
    }

    public void testSelectionInsideTheString() {
        checkSelectionInsideTheString("SelectionInsideTheString.java");
        checkSelectionInsideTheString("SelectionInsideTheString.py");
    }

    private void checkSelectionInsideTheString(String fileName) {
        myFixture.configureByText(fileName, "");
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();
        Project project = getProject();
        TestHelper.checkWithConsumer(TestCase::assertNotNull, project, psiFile, editor);

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

    private void checkUnInjectionAvailability(final String fileName, @NotNull CheckWithInjectedLanguage checker) {
        Project project = getProject();
        myFixture.configureByText(fileName, "");
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();
        TestHelper.checkWithConsumer(TestCase::assertNotNull, project, psiFile, editor);
        checker.check(project, psiFile, editor);
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
