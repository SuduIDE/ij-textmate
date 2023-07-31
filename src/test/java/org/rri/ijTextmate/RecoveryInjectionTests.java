package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Listeners.InitializerHighlightListener;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;

public class RecoveryInjectionTests extends BasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/PersistentStorageCases";
    }

    public void test0Clear() {
        PersistentStorage.getInstance(getProject()).getState().clear();
    }

    public void testWithoutMoving() {
        checkRecoveryInjection("WithoutMoving.before.java",
                "WithoutMoving.after.java",
                "WithoutMoving.java",
                getMessage("testWithoutMoving"));
    }

    public void testWithMovingExpressionSimple() {
        checkRecoveryInjection("WithMovingExpressionSimple.before.java",
                "WithMovingExpressionSimple.after.java",
                "WithMovingExpressionSimple.java",
                getMessage("testWithMovingExpressionSimple"));
    }

    public void testWithMovingExpressionHard() {
        checkRecoveryInjection("WithMovingExpressionHard.before.java",
                "WithMovingExpressionHard.after.java",
                "WithMovingExpressionHard.java",
                getMessage("testWithMovingExpressionHard"));
    }

    public void testWithRename() {
        checkRecoveryInjection("WithRename.before.java",
                "WithRename.after.java",
                "WithRename.java",
                getMessage("testWithRename"));
    }

    private void checkRecoveryInjection(String beforeName, String afterName, String baseName, String message) {
        PsiFile[] psiFiles = myFixture.configureByFiles(beforeName, afterName);
        myFixture.renameElement(psiFiles[0], baseName);
        assertFalse(isInjected(getProject(), myFixture.getEditor(), psiFiles[0]));

        TestHelper.injectLanguage(getProject(), myFixture.getEditor(), psiFiles[0]);
        assertTrue(isInjected(getProject(), myFixture.getEditor(), psiFiles[0]));

        myFixture.renameElement(psiFiles[0], beforeName);
        myFixture.renameElement(psiFiles[1], baseName);

        new InitializerHighlightListener(getProject()).fileOpened(FileEditorManager.getInstance(getProject()), psiFiles[1].getVirtualFile());
        myFixture.openFileInEditor(psiFiles[1].getVirtualFile());
        assertTrue(message, isInjected(getProject(), myFixture.getEditor(), psiFiles[1]));
    }

    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, editor.getCaretModel().getOffset());
        return element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());
    }

    private String getMessage(String testName) {
        return String.format("\nTest name: %s\nMessage: Injection for literal must persist after modification in another editor\n", testName);
    }
}
