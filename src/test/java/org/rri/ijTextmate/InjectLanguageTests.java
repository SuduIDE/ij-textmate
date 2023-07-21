package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.TemporaryPlace;

import static org.rri.ijTextmate.TestHelper.ASSERT_FALSE;
import static org.rri.ijTextmate.TestHelper.ASSERT_TRUE;

public class InjectLanguageTests extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionAvailabilityCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        checkInjectLanguage("CaretInTheCenterInsideTheString.java", ASSERT_TRUE);
        checkInjectLanguage("CaretInTheCenterInsideTheString.py", ASSERT_TRUE);
        checkInjectLanguage("CaretInTheCenterInsideTheString.json", ASSERT_TRUE);
    }

    @Test
    public void testCaretOnTheLeftInsideTheString() {
        checkInjectLanguage("CaretOnTheLeftInsideTheString.java", ASSERT_TRUE);
        checkInjectLanguage("CaretOnTheLeftInsideTheString.py", ASSERT_TRUE);
        checkInjectLanguage("CaretOnTheLeftInsideTheString.json", ASSERT_TRUE);
    }

    @Test
    public void testCaretOnTheRightInsideTheString() {
        checkInjectLanguage("CaretOnTheRightInsideTheString.java", ASSERT_TRUE);
        checkInjectLanguage("CaretOnTheRightInsideTheString.py", ASSERT_TRUE);
        checkInjectLanguage("CaretOnTheRightInsideTheString.json", ASSERT_TRUE);
    }

    @Test
    public void testCaretOnTheLeftOutsideOfTheString() {
        checkInjectLanguage("CaretOnTheLeftOutsideOfTheString.java", ASSERT_FALSE);
        checkInjectLanguage("CaretOnTheLeftOutsideOfTheString.py", ASSERT_FALSE);
        checkInjectLanguage("CaretOnTheLeftOutsideOfTheString.json", ASSERT_FALSE);
    }

    @Test
    public void testCaretOnTheRightOutsideOfTheString() {
        checkInjectLanguage("CaretOnTheRightOutsideOfTheString.java", ASSERT_FALSE);
        checkInjectLanguage("CaretOnTheRightOutsideOfTheString.py", ASSERT_FALSE);
        checkInjectLanguage("CaretOnTheRightOutsideOfTheString.json", ASSERT_FALSE);
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
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, editor.getCaretModel().getOffset());
        boolean resFirst = element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(project).getState();
        return resFirst && elements.contains(new TemporaryPlace(TestHelper.INJECTED_LANGUAGE, editor.getCaretModel().getOffset()));
    }
}
