package org.rri.ijTextmate;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.Map;

public class InjectLanguageTests extends LightPlatformCodeInsightFixture4TestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        String testName = "testCaretInTheCenterInsideTheString";
        String javaFileName = "CaretInTheCenterInsideTheString.java";

        String messageForJava = getMessageIfLanguageCanBeInjected(testName, javaFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertTrueWithMessage(messageForJava));
    }

    @Test
    public void testCaretOnTheLeftInsideTheString() {
        String testName = "testCaretOnTheLeftInsideTheString";
        String javaFileName = "CaretOnTheLeftInsideTheString.java";

        String messageForJava = getMessageIfLanguageCanBeInjected(testName, javaFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertTrueWithMessage(messageForJava));
    }

    @Test
    public void testCaretOnTheRightInsideTheString() {
        String testName = "testCaretOnTheRightInsideTheString";
        String javaFileName = "CaretOnTheRightInsideTheString.java";

        String messageForJava = getMessageIfLanguageCanBeInjected(testName, javaFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertTrueWithMessage(messageForJava));
    }

    @Test
    public void testCaretOnTheLeftOutsideOfTheString() {
        String testName = "testCaretOnTheLeftOutsideOfTheString";
        String javaFileName = "CaretOnTheLeftOutsideOfTheString.java";

        String messageForJava = getMessageIfLanguageCanNotBeInjected(testName, javaFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertFalseWithMessage(messageForJava));
    }

    @Test
    public void testCaretOnTheRightOutsideOfTheString() {
        String testName = "testCaretOnTheRightOutsideOfTheString";
        String javaFileName = "CaretOnTheRightOutsideOfTheString.java";

        String messageForJava = getMessageIfLanguageCanNotBeInjected(testName, javaFileName);

        checkInjectLanguage(javaFileName, TestHelper.createAssertFalseWithMessage(messageForJava));
    }

    private String getMessageIfLanguageCanNotBeInjected(String testName, String fileName) {
        return TestHelper.getMessage(testName, fileName, "Language can not be injected in this literal. But the test says it can");
    }

    private String getMessageIfLanguageCanBeInjected(String testName, String fileName) {
        return TestHelper.getMessage(testName, fileName, "Language can be injected in this literal. But the test says it can't");
    }

    private void checkInjectLanguage(String fileName, TestHelper.@NotNull Assert checking) {
        Project project = myFixture.getProject();
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = myFixture.getEditor();

        TestHelper.injectLanguage(project, editor, psiFile);
        checking.test(isInjected(project, editor, psiFile));
    }

    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        boolean resultFirst = TestHelper.isInjected(project, editor, psiFile);

        String relivePath = InjectorHelper.gitRelativePath(project, psiFile).toString();
        var map = TemporaryStorage.getInstance(project).get(relivePath).getMap();

        PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());

        assertNotNull(getMessage(psiFile.getName()), psiElement);

        return resultFirst && intersectsWithElementFromMap(map, psiElement);
    }

    private boolean intersectsWithElementFromMap(@NotNull Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, String> map, @NotNull PsiElement psiElement) {
        TextRange textRange = psiElement.getTextRange();
        for (var key : map.keySet()) {
            PsiElement element = key.getElement();
            if (element == null) continue;
            TextRange textRangeOfElement = element.getTextRange();
            if (textRangeOfElement != null && textRange.intersects(textRangeOfElement)) return true;
        }
        return false;
    }

    private String getMessage(String fileName) {
        return String.format("\nFile: %s\nMessage: psiElement is null", fileName);
    }
}
