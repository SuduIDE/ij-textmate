package org.rri.ijTextmate;

import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.rri.ijTextmate.Helpers.InjectorHelper;

public class InjectionAvailabilityTests extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionAvailabilityCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        checkCaret("CaretInTheCenterInsideTheString.java", ASSERT_TRUE);
    }

    @Test
    public void testCaretOnTheLeftInsideTheString() {
        checkCaret("CaretOnTheLeftInsideTheString.java", ASSERT_TRUE);
    }

    @Test
    public void testCaretOnTheRightInsideTheString() {
        checkCaret("CaretOnTheRightInsideTheString.java", ASSERT_TRUE);
    }

    @Test
    public void testCaretOnTheLeftOutsideOfTheString() {
        checkCaret("CaretOnTheLeftOutsideOfTheString.java", ASSERT_FALSE);
    }

    @Test
    public void testCaretOnTheRightOutsideOfTheString() {
        checkCaret("CaretOnTheRightOutsideOfTheString.java", ASSERT_FALSE);
    }

    @Test
    public void testSelectionInsideTheString() {
        PsiFile psiFile = myFixture.configureByFile("SelectionInsideTheString.java");
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
                assertTrue(canInjectLanguageToHost(project, psiFile, editor));
            } while (++i < end);
        }
    }

    @Contract(pure = true)
    private void checkNotNull(Object @NotNull ... args) {
        for (Object arg : args) {
            assertNotNull(arg);
        }
    }

    private void checkCaret(final String fileName, @NotNull Assert test) {
        Project project = getProject();
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();
        checkNotNull(project, psiFile, editor);
        test.test(canInjectLanguageToHost(project, psiFile, editor));
    }

    private boolean canInjectLanguageToHost(Project project, PsiFile psiFile, Editor editor) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);
        InjectLanguageAction action = new InjectLanguageAction();
        if (!action.canInjectLanguageToHost(project, editor, psiFile, host)) return false;
        host = InjectorHelper.resolveHost(host);
        return action.canInjectLanguageToHost(project, editor, psiFile, host);
    }

    private final Assert ASSERT_FALSE = TestCase::assertFalse;
    private final Assert ASSERT_TRUE = TestCase::assertTrue;

    @FunctionalInterface
    private interface Assert {
        void test(boolean b);
    }
}
