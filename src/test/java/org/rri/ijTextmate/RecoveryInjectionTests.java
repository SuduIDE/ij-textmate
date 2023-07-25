package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;

public class RecoveryInjectionTests extends BasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/PersistentStorageCases";
    }

    public void test0Clear() {
        PersistentStorage.getInstance(getProject()).getState().clear();
    }

    public void testWithoutMoving() {
        checkRecoveryInjection("WithoutMoving.before.java", "WithoutMoving.after.java", "WithoutMoving.java");
    }

    public void testWithMovingExpressionSimple() {
        checkRecoveryInjection("WithMovingExpressionSimple.before.java", "WithMovingExpressionSimple.after.java", "WithMovingExpressionSimple.java");
    }

    public void testWithMovingExpressionHard() {
        checkRecoveryInjection("WithMovingExpressionHard.before.java", "WithMovingExpressionHard.after.java", "WithMovingExpressionHard.java");
    }

    public void testWithRename() {
        checkRecoveryInjection("WithRename.before.java", "WithRename.after.java", "WithRename.java");
    }

    private void checkRecoveryInjection(String beforeName, String afterName, String baseName) {
        PsiFile[] psiFiles = myFixture.configureByFiles(beforeName, afterName);
        myFixture.renameElement(psiFiles[0], baseName);
        assertFalse(isInjected(getProject(), myFixture.getEditor(), psiFiles[0]));
        TestHelper.injectLanguage(getProject(), myFixture.getEditor(), psiFiles[0], getTestRootDisposable());
        assertTrue(isInjected(getProject(), myFixture.getEditor(), psiFiles[0]));
        myFixture.renameElement(psiFiles[0], beforeName);
        myFixture.renameElement(psiFiles[1], baseName);
        new InitializerHighlight(getProject()).fileOpened(FileEditorManager.getInstance(getProject()), psiFiles[1].getVirtualFile());
        myFixture.openFileInEditor(psiFiles[1].getVirtualFile());
        isInjected(getProject(), myFixture.getEditor(), psiFiles[1]);
        assertTrue(isInjected(getProject(), myFixture.getEditor(), psiFiles[1]));
    }

    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, editor.getCaretModel().getOffset());
        return element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());
    }
}
