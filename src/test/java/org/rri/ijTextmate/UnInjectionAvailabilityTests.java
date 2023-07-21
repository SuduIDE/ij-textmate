package org.rri.ijTextmate;

import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Inject.InjectLanguage;

public class UnInjectionAvailabilityTests extends LightJavaCodeInsightFixtureTestCase {
    private final static String INJECTED_LANGUAGE = "sql";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionAvailabilityCases";
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
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();
        Project project = getProject();
        checkNotNull(project, psiFile, editor);
        for (CaretState caretState : editor.getCaretModel().getCaretsAndSelections()) {
            checkNotNull(caretState.getSelectionStart(), caretState.getSelectionEnd());
            int i = caretState.getSelectionStart().column;
            int end = caretState.getSelectionEnd().column;
            int line = caretState.getSelectionStart().line;
            do {
                editor.getCaretModel().moveToVisualPosition(new VisualPosition(line, i));
                checkNotNull(project, psiFile, editor);
                caretInsideString.check(project, psiFile, editor);
            } while (++i < end);
        }
    }

    @Contract(pure = true)
    private void checkNotNull(Object @NotNull ... args) {
        for (Object arg : args) {
            assertNotNull(arg);
        }
    }

    private void checkUnInjectionAvailability(final String fileName, @NotNull CheckWithInjectedLanguage checker) {
        Project project = getProject();
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();
        checkNotNull(project, psiFile, editor);
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
