package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.Storage.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.Storage.PersistentStorage.SetElement;

public class UnInjectLanguageTests extends JavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    public void testCaretInTheCenterInsideTheString() {
        checkUnInjectLanguage("CaretInTheCenterInsideTheString.java");
        checkUnInjectLanguage("CaretInTheCenterInsideTheString.py");
    }

    public void testCaretOnTheLeftInsideTheString() {
        checkUnInjectLanguage("CaretOnTheLeftInsideTheString.java");
        checkUnInjectLanguage("CaretOnTheLeftInsideTheString.py");
    }

    public void testCaretOnTheRightInsideTheString() {
        checkUnInjectLanguage("CaretOnTheRightInsideTheString.java");
        checkUnInjectLanguage("CaretOnTheRightInsideTheString.py");
    }

    private void checkUnInjectLanguage(String fileName) {
        myFixture.configureByText(fileName, "");
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Project project = getProject();
        Editor editor = myFixture.getEditor();

        TestHelper.checkWithConsumer(TestCase::assertNotNull, project, psiFile, editor);
        TestHelper.injectLanguage(project, editor, psiFile);

        assertTrue(isInjected(project, editor, psiFile));

        UnInjectLanguageAction.unInjectLanguage(project, editor, psiFile);

        String message = String.format("\nFile: %s\nMessage: the literal must not contain an injection after deletion\n", fileName);
        assertFalse(message, isInjected(project, editor, psiFile));
    }


    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        PsiManager.getInstance(project).dropPsiCaches();
        final int offset = editor.getCaretModel().getOffset();
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, offset);
        boolean resFirst = element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());

        String relivePath = InjectorHelper.gitRelativePath(project, psiFile).toString();
        SetElement elements = PersistentStorage.getInstance(project).getState().get(relivePath);
        PsiElement psiElement = psiFile.findElementAt(offset);

        String message = String.format("\nFile: %s\nMessage: psiElements is null", psiFile.getName());
        assertNotNull(message, psiElement);

        return resFirst || elements.contains(new PlaceInjection(TestHelper.INJECTED_LANGUAGE, psiElement.getTextRange()));
    }
}
