package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.PersistentStorage.SetElement;

import static org.rri.ijTextmate.TestHelper.ASSERT_FALSE;
import static org.rri.ijTextmate.TestHelper.ASSERT_TRUE;

public class InjectLanguageTests extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    public void testCaretInTheCenterInsideTheString() {
        checkInjectLanguage("CaretInTheCenterInsideTheString.java", ASSERT_TRUE);
        checkInjectLanguage("CaretInTheCenterInsideTheString.py", ASSERT_TRUE);
    }

    public void testCaretOnTheLeftInsideTheString() {
        checkInjectLanguage("CaretOnTheLeftInsideTheString.java", ASSERT_TRUE);
        checkInjectLanguage("CaretOnTheLeftInsideTheString.py", ASSERT_TRUE);
    }

    public void testCaretOnTheRightInsideTheString() {
        checkInjectLanguage("CaretOnTheRightInsideTheString.java", ASSERT_TRUE);
        checkInjectLanguage("CaretOnTheRightInsideTheString.py", ASSERT_TRUE);
    }

    public void testCaretOnTheLeftOutsideOfTheString() {
        checkInjectLanguage("CaretOnTheLeftOutsideOfTheString.java", ASSERT_FALSE);
        checkInjectLanguage("CaretOnTheLeftOutsideOfTheString.py", ASSERT_FALSE);
    }

    public void testCaretOnTheRightOutsideOfTheString() {
        checkInjectLanguage("CaretOnTheRightOutsideOfTheString.java", ASSERT_FALSE);
        checkInjectLanguage("CaretOnTheRightOutsideOfTheString.py", ASSERT_FALSE);
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
        String relivePath = InjectorHelper.gitRelativePath(project, psiFile).toString();
        SetElement elements = PersistentStorage.getInstance(project).getState().get(relivePath);
        return resFirst && elements.contains(new PlaceInjection(TestHelper.INJECTED_LANGUAGE, editor.getCaretModel().getOffset()));
    }
}
