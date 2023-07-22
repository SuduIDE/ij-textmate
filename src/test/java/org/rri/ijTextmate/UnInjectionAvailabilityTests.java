package org.rri.ijTextmate;

import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class UnInjectionAvailabilityTests extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        checkUnInjectionAvailability("CaretInTheCenterInsideTheString.java", caretInsideString);
        checkUnInjectionAvailability("CaretInTheCenterInsideTheString.py", caretInsideString);
        checkUnInjectionAvailability("CaretInTheCenterInsideTheString.json", caretInsideString);
    }

    @Test
    public void testCaretOnTheLeftInsideTheString() {
        checkUnInjectionAvailability("CaretOnTheLeftInsideTheString.java", caretInsideString);
        checkUnInjectionAvailability("CaretOnTheLeftInsideTheString.py", caretInsideString);
        checkUnInjectionAvailability("CaretOnTheLeftInsideTheString.json", caretInsideString);
    }

    @Test
    public void testCaretOnTheRightInsideTheString() {
        checkUnInjectionAvailability("CaretOnTheRightInsideTheString.java", caretInsideString);
        checkUnInjectionAvailability("CaretOnTheRightInsideTheString.py", caretInsideString);
        checkUnInjectionAvailability("CaretOnTheRightInsideTheString.json", caretInsideString);
    }

    @Test
    public void testCaretOnTheLeftOutsideOfTheString() {
        checkUnInjectionAvailability("CaretOnTheLeftOutsideOfTheString.java", caretOutsideString);
        checkUnInjectionAvailability("CaretOnTheLeftOutsideOfTheString.py", caretOutsideString);
        checkUnInjectionAvailability("CaretOnTheLeftOutsideOfTheString.json", caretOutsideString);
    }

    @Test
    public void testCaretOnTheRightOutsideOfTheString() {
        checkUnInjectionAvailability("CaretOnTheRightOutsideOfTheString.java", caretOutsideString);
        checkUnInjectionAvailability("CaretOnTheRightOutsideOfTheString.py", caretOutsideString);
        checkUnInjectionAvailability("CaretOnTheRightOutsideOfTheString.json", caretOutsideString);
    }

    @Test
    public void testSelectionInsideTheString() {
        checkSelectionInsideTheString("SelectionInsideTheString.java");
        checkSelectionInsideTheString("SelectionInsideTheString.py");
        checkSelectionInsideTheString("SelectionInsideTheString.json");
    }

    private void checkSelectionInsideTheString(String fileName) {
        myFixture.configureByText(fileName, "");
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();
        Project project = getProject();
        TestHelper.checkWithConsumer(TestCase::assertNotNull, project, psiFile, editor);
        for (CaretState caretState : editor.getCaretModel().getCaretsAndSelections()) {
            TestHelper.checkWithConsumer(TestCase::assertNotNull, caretState.getSelectionStart(), caretState.getSelectionEnd());
            int i = caretState.getSelectionStart().column;
            int end = caretState.getSelectionEnd().column;
            int line = caretState.getSelectionStart().line;
            do {
                editor.getCaretModel().moveToVisualPosition(new VisualPosition(line, i));
                TestHelper.checkWithConsumer(TestCase::assertNotNull, project, psiFile, editor);
                caretInsideString.check(project, psiFile, editor);
            } while (++i < end);
        }
    }

    private void checkUnInjectionAvailability(final String fileName, @NotNull CheckWithInjectedLanguage checker) {
        getPsiManager().dropPsiCaches();
        PsiManager.getInstance(getProject()).dropPsiCaches();
        Project project = getProject();
        myFixture.configureByText(fileName, "");
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();
        TestHelper.checkWithConsumer(TestCase::assertNotNull,  project, psiFile, editor);
        checker.check(project, psiFile, editor);
    }

    private boolean canUnInjectLanguageToHost(Project project, PsiFile psiFile, Editor editor) {
        PsiLanguageInjectionHost host = TestHelper.getHost(editor, psiFile);
        UnInjectLanguageAction action = new UnInjectLanguageAction();
        return action.canUnInjectLanguageToHost(project, editor, psiFile, host);
    }

    private final CheckWithInjectedLanguage caretInsideString = (Project project, PsiFile psiFile, Editor editor) -> {
        TestHelper.injectLanguage(project, editor, psiFile, getTestRootDisposable());
        assertTrue(canUnInjectLanguageToHost(project, psiFile, editor));
    };

    private final CheckWithInjectedLanguage caretOutsideString = (Project project, PsiFile psiFile, Editor editor) -> {
        assertFalse(canUnInjectLanguageToHost(project, psiFile, editor));
    };

    @FunctionalInterface
    private interface CheckWithInjectedLanguage {
        void check(Project project, PsiFile psiFile, Editor editor);
    }
}
