package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.PersistentStorage.SetElement;

public class InjectLanguageTests extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    public void testCaretInTheCenterInsideTheString() {
        String testName = "testCaretInTheCenterInsideTheString";
        String javaFileName = "CaretInTheCenterInsideTheString.java";
        String pythonFileName = "CaretInTheCenterInsideTheString.py";

        String messageForJava = getMessageIfLanguageCanBeInjected(testName, javaFileName);
        String messageForPython = getMessageIfLanguageCanBeInjected(testName, pythonFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertTrueWithMessage(messageForJava));
        checkInjectLanguage(pythonFileName, TestHelper.createAssertTrueWithMessage(messageForPython));
    }

    public void testCaretOnTheLeftInsideTheString() {
        String testName = "testCaretOnTheLeftInsideTheString";
        String javaFileName = "CaretOnTheLeftInsideTheString.java";
        String pythonFileName = "CaretOnTheLeftInsideTheString.py";

        String messageForJava = getMessageIfLanguageCanBeInjected(testName, javaFileName);
        String messageForPython = getMessageIfLanguageCanBeInjected(testName, pythonFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertTrueWithMessage(messageForJava));
        checkInjectLanguage(pythonFileName, TestHelper.createAssertTrueWithMessage(messageForPython));
    }

    public void testCaretOnTheRightInsideTheString() {
        String testName = "testCaretOnTheRightInsideTheString";
        String javaFileName = "CaretOnTheRightInsideTheString.java";
        String pythonFileName = "CaretOnTheRightInsideTheString.py";

        String messageForJava = getMessageIfLanguageCanBeInjected(testName, javaFileName);
        String messageForPython = getMessageIfLanguageCanBeInjected(testName, pythonFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertTrueWithMessage(messageForJava));
        checkInjectLanguage(pythonFileName, TestHelper.createAssertTrueWithMessage(messageForPython));
    }

    public void testCaretOnTheLeftOutsideOfTheString() {
        String testName = "testCaretOnTheLeftOutsideOfTheString";
        String javaFileName = "CaretOnTheLeftOutsideOfTheString.java";
        String pythonFileName = "CaretOnTheLeftOutsideOfTheString.py";

        String messageForJava = getMessageIfLanguageCanNotBeInjected(testName, javaFileName);
        String messageForPython = getMessageIfLanguageCanNotBeInjected(testName, pythonFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertFalseWithMessage(messageForJava));
        checkInjectLanguage(pythonFileName, TestHelper.createAssertFalseWithMessage(messageForPython));
    }

    public void testCaretOnTheRightOutsideOfTheString() {
        String testName = "testCaretOnTheRightOutsideOfTheString";
        String javaFileName = "CaretOnTheRightOutsideOfTheString.java";
        String pythonFileName = "CaretOnTheRightOutsideOfTheString.py";

        String messageForJava = getMessageIfLanguageCanNotBeInjected(testName, javaFileName);
        String messageForPython = getMessageIfLanguageCanNotBeInjected(testName, pythonFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertFalseWithMessage(messageForJava));
        checkInjectLanguage(pythonFileName, TestHelper.createAssertFalseWithMessage(messageForPython));
    }

    private String getMessageIfLanguageCanNotBeInjected(String testName, String fileName) {
        return TestHelper.getMessage(testName, fileName, "Language can not be injected in this literal. But the test says it can");
    }

    private String getMessageIfLanguageCanBeInjected(String testName, String fileName) {
        return TestHelper.getMessage(testName, fileName, "Language can be injected in this literal. But the test says it can't");
    }


    private void checkInjectLanguage(String fileName, TestHelper.@NotNull Assert test) {
        Project project = getProject();
        myFixture.configureByText(fileName, "");
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();

        TestHelper.checkWithConsumer(TestCase::assertNotNull, psiFile, project, editor);
        TestHelper.injectLanguage(project, editor, psiFile, getTestRootDisposable());
        test.test(isInjected(project, editor, psiFile));
    }

    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        PsiManager.getInstance(project).dropPsiCaches();
        final int offset = editor.getCaretModel().getOffset();
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, offset);
        boolean resFirst = element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());

        String relivePath = InjectorHelper.gitRelativePath(project, psiFile).toString();
        SetElement elements = PersistentStorage.getInstance(project).getState().get(relivePath);
        PsiElement psiElement = psiFile.findElementAt(offset);

        String message = String.format("\nFile: %s\nMessage: psiElemens is null", psiFile.getName());
        assertNotNull(message, psiElement);

        return resFirst && elements.contains(new PlaceInjection(TestHelper.INJECTED_LANGUAGE, psiElement.getTextRange()));
    }
}
