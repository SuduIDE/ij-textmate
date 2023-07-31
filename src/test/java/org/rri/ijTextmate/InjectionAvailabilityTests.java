package org.rri.ijTextmate;

import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.testFramework.TestDataPath;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;

import java.util.Objects;

@TestDataPath("$CONTENT_ROOT/testData")
public class InjectionAvailabilityTests extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    public void testCaretInTheCenterInsideTheString() {
        String testName = "testCaretInTheCenterInsideTheString";
        String javaFileName = "CaretInTheCenterInsideTheString.java";

        String messageForJava = getMessageIfLanguageCanBeInjected(testName, javaFileName);

        checkInjectionAvailability(javaFileName, TestHelper.createAssertTrueWithMessage(messageForJava));
    }

    public void testCaretOnTheLeftInsideTheString() {
        String testName = "testCaretOnTheLeftInsideTheString";
        String javaFileName = "CaretOnTheLeftInsideTheString.java";

        String messageForJava = getMessageIfLanguageCanBeInjected(testName, javaFileName);

        checkInjectionAvailability(javaFileName, TestHelper.createAssertTrueWithMessage(messageForJava));
    }

    public void testCaretOnTheRightInsideTheString() {
        String testName = "testCaretOnTheRightInsideTheString";
        String javaFileName = "CaretOnTheRightInsideTheString.java";

        String messageForJava = getMessageIfLanguageCanBeInjected(testName, javaFileName);

        checkInjectionAvailability(javaFileName, TestHelper.createAssertTrueWithMessage(messageForJava));
    }

    public void testCaretOnTheLeftOutsideOfTheString() {
        String testName = "testCaretOnTheLeftOutsideOfTheString";
        String javaFileName = "CaretOnTheLeftOutsideOfTheString.java";

        String messageForJava = getMessageIfLanguageCanNotBeInjected(testName, javaFileName);

        checkInjectionAvailability(javaFileName, TestHelper.createAssertFalseWithMessage(messageForJava));
    }

    public void testCaretOnTheRightOutsideOfTheString() {
        String testName = "testCaretOnTheRightOutsideOfTheString";
        String javaFileName = "CaretOnTheRightOutsideOfTheString.java";

        String messageForJava = getMessageIfLanguageCanNotBeInjected(testName, javaFileName);

        checkInjectionAvailability(javaFileName, TestHelper.createAssertFalseWithMessage(messageForJava));
    }

    public void testSelectionInsideTheString() {
        myFixture.configureByText("SelectionInsideTheString.java", "");
        PsiFile psiFile = myFixture.configureByFile("SelectionInsideTheString.java");
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
                assertTrue(getMessageIfLanguageCanBeInjected("SelectionInsideTheString", "SelectionInsideTheString.java"), canInjectLanguageToHost(project, psiFile, editor))
                ;
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

    private String getMessageIfLanguageCanNotBeInjected(String testName, String fileName) {
        return TestHelper.getMessage(testName, fileName, "Language can not be injected in this literal. But the test says it can");
    }

    private String getMessageIfLanguageCanBeInjected(String testName, String fileName) {
        return TestHelper.getMessage(testName, fileName, "Language can be injected in this literal. But the test says it can't");
    }
}
