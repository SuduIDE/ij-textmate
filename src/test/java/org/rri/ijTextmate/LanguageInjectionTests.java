package org.rri.ijTextmate;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.Map;

public class LanguageInjectionTests extends LightPlatformCodeInsightFixture4TestCase {
    private static final int CENTER_INSIDE_OFFSET = 131;
    private static final int LEFT_INSIDE_OFFSET = 99;
    private static final int RIGHT_INSIDE_OFFSET = 153;
    private static final int LEFT_OUTSIDE_OFFSET = 98;
    private static final int RIGHT_OUTSIDE_OFFSET = 160;
    private static final String JAVA_FILE_SUCCESS = "LanguageInjectionTestCaseSuccess.java";
    private static final String JAVA_FILE_FAIL = "LanguageInjectionTestCaseFail.java";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases/LanguageInjectionTestCases";
    }

    @Test
    public void testCaretInsideStringLiteralCentered() {
        String message = getMessageIfLanguageCanBeInjected(JAVA_FILE_SUCCESS);

        assertTrue(message, checkInjectLanguage(JAVA_FILE_SUCCESS, CENTER_INSIDE_OFFSET));
    }

    @Test
    public void caretInsideStringLiteralOnLeft() {
        String message = getMessageIfLanguageCanBeInjected(JAVA_FILE_SUCCESS);

        assertTrue(message, checkInjectLanguage(JAVA_FILE_SUCCESS, LEFT_INSIDE_OFFSET));
    }

    @Test
    public void caretInsideStringLiteralOnRight() {
        String message = getMessageIfLanguageCanBeInjected(JAVA_FILE_SUCCESS);

        assertTrue(message, checkInjectLanguage(JAVA_FILE_SUCCESS, RIGHT_INSIDE_OFFSET));
    }

    @Test
    public void caretOutsideStringLiteralOnLeft() {
        String message = getMessageIfLanguageCanNotBeInjected(JAVA_FILE_FAIL);

        assertFalse(message, checkInjectLanguage(JAVA_FILE_SUCCESS, LEFT_OUTSIDE_OFFSET));
    }

    @Test
    public void caretOutsideStringLiteralOnRight() {
        String message = getMessageIfLanguageCanNotBeInjected(JAVA_FILE_FAIL);

        assertFalse(message, checkInjectLanguage(JAVA_FILE_SUCCESS, RIGHT_OUTSIDE_OFFSET));
    }

    @SuppressWarnings("SameParameterValue")
    private String getMessageIfLanguageCanNotBeInjected(String fileName) {
        return TestHelper.getMessage(fileName, "Language can not be injected in this literal. But the test says it can");
    }

    @SuppressWarnings("SameParameterValue")
    private String getMessageIfLanguageCanBeInjected(String fileName) {
        return TestHelper.getMessage(fileName, "Language can be injected in this literal. But the test says it can't");
    }

    @SuppressWarnings("SameParameterValue")
    private boolean checkInjectLanguage(String fileName, int offset) {
        PsiFile psiFile = myFixture.getFile();

        if (psiFile == null) {
            psiFile = myFixture.configureByFile(fileName);
        }

        Project project = myFixture.getProject();
        Editor editor = myFixture.getEditor();

        editor.getCaretModel().moveToOffset(offset);

        TestHelper.injectLanguage(project, editor, psiFile);

        return isInjected(project, editor, psiFile);
    }

    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        boolean resultFirst = TestHelper.isInjected(project, editor, psiFile);

        String relivePath = InjectorHelper.getRelativePath(project, psiFile);
        var map = TemporaryStorage.getInstance(project).get(relivePath).getMap();

        PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());

        assertNotNull(getMessage(psiFile.getName()), psiElement);

        return resultFirst && intersectsWithElementFromMap(map, psiElement);
    }

    private boolean intersectsWithElementFromMap(@NotNull Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection> map, @NotNull PsiElement psiElement) {
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
