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
import org.rri.ijTextmate.Helpers.InjectorHelper;

import java.util.Objects;

import static org.rri.ijTextmate.TestHelper.ASSERT_TRUE;
import static org.rri.ijTextmate.TestHelper.ASSERT_FALSE;

public class InjectionAvailabilityTests extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    public void testCaretInTheCenterInsideTheString() {
        checkInjectionAvailability("CaretInTheCenterInsideTheString.java", ASSERT_TRUE);
        checkInjectionAvailability("CaretInTheCenterInsideTheString.py", ASSERT_TRUE);
    }

    public void testCaretOnTheLeftInsideTheString() {
        checkInjectionAvailability("CaretOnTheLeftInsideTheString.java", ASSERT_TRUE);
        checkInjectionAvailability("CaretOnTheLeftInsideTheString.py", ASSERT_TRUE);
    }

    public void testCaretOnTheRightInsideTheString() {
        checkInjectionAvailability("CaretOnTheRightInsideTheString.java", ASSERT_TRUE);
        checkInjectionAvailability("CaretOnTheRightInsideTheString.py", ASSERT_TRUE);
    }

    public void testCaretOnTheLeftOutsideOfTheString() {
        checkInjectionAvailability("CaretOnTheLeftOutsideOfTheString.java", ASSERT_FALSE);
        checkInjectionAvailability("CaretOnTheLeftOutsideOfTheString.py", ASSERT_FALSE);
    }

    public void testCaretOnTheRightOutsideOfTheString() {
        checkInjectionAvailability("CaretOnTheRightOutsideOfTheString.java", ASSERT_FALSE);
        checkInjectionAvailability("CaretOnTheRightOutsideOfTheString.py", ASSERT_FALSE);
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
                assertTrue(canInjectLanguageToHost(project, psiFile, editor));
            } while (++i < end);
        }
    }

    private void checkInjectionAvailability(final String fileName, @NotNull TestHelper.Assert test) {
        Project project = getProject();
        myFixture.configureByText(fileName, "");
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();
        TestHelper.checkWithConsumer(TestCase::assertNotNull, project, psiFile, editor);
        test.test(canInjectLanguageToHost(project, psiFile, editor));
    }

    private boolean canInjectLanguageToHost(Project project, PsiFile psiFile, Editor editor) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);
        InjectLanguageAction action = new InjectLanguageAction();
        if (!action.canInjectLanguageToHost(project, editor, psiFile, host)) return false;
        host = InjectorHelper.resolveHost(host);
        return action.canInjectLanguageToHost(project, editor, psiFile, host);
    }
}
