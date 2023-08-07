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

public class LanguageInjectorMainTests extends LightPlatformCodeInsightFixture4TestCase {
    private static final int CENTER_INSIDE_OFFSET = 131;
    private static final int LEFT_INSIDE_OFFSET = 99;
    private static final int RIGHT_INSIDE_OFFSET = 153;
    private static final int LEFT_OUTSIDE_OFFSET = 98;
    private static final int RIGHT_OUTSIDE_OFFSET = 160;
    private static final String JAVA_FILE = "LanguageInjectionTestCase.java";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void testLanguageInjector() {
        String messageForJava = getMessageIfLanguageCanBeInjected();

        checkInjectLanguage(TestHelper.createAssertTrueWithMessage(messageForJava), CENTER_INSIDE_OFFSET);

        checkInjectLanguage(TestHelper.createAssertTrueWithMessage(messageForJava), LEFT_INSIDE_OFFSET);

        checkInjectLanguage(TestHelper.createAssertTrueWithMessage(messageForJava), RIGHT_INSIDE_OFFSET);


        messageForJava = getMessageIfLanguageCanNotBeInjected();

        checkInjectLanguage(TestHelper.createAssertFalseWithMessage(messageForJava), LEFT_OUTSIDE_OFFSET);

        checkInjectLanguage(TestHelper.createAssertFalseWithMessage(messageForJava), RIGHT_OUTSIDE_OFFSET);
    }

    private String getMessageIfLanguageCanNotBeInjected() {
        return TestHelper.getMessage(LanguageInjectorMainTests.JAVA_FILE, "Language can not be injected in this literal. But the test says it can");
    }

    private String getMessageIfLanguageCanBeInjected() {
        return TestHelper.getMessage(LanguageInjectorMainTests.JAVA_FILE, "Language can be injected in this literal. But the test says it can't");
    }

    private void checkInjectLanguage(TestHelper.@NotNull Assert checking, int offset) {
        PsiFile psiFile = myFixture.getFile();

        if (psiFile == null) {
            psiFile = myFixture.configureByFile(JAVA_FILE);
        }

        Project project = myFixture.getProject();
        Editor editor = myFixture.getEditor();

        editor.getCaretModel().moveToOffset(offset);

        TestHelper.injectLanguage(project, editor, psiFile);
        checking.test(isInjected(project, editor, psiFile));
    }

    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        boolean resultFirst = TestHelper.isInjected(project, editor, psiFile);

        String relivePath = InjectorHelper.getRelativePath(project, psiFile);
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
