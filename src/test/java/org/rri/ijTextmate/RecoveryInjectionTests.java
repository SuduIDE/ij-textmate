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

/*
    Uncomment after changing heuristics

    public void testWithMovingExpressionHard() {
        checkRecoveryInjection("WithMovingExpressionHard.before.java",
                "WithMovingExpressionHard.after.java",
                "WithMovingExpressionHard.java",
                getMessage("testWithMovingExpressionHard"));
    }

    public void testWithMovingExpressionSimple() {
        checkRecoveryInjection("WithMovingExpressionSimple.before.java",
                "WithMovingExpressionSimple.after.java",
                "WithMovingExpressionSimple.java",
                getMessage("testWithMovingExpressionSimple"));
    }
*/


    public void testWithoutMoving() {
        boolean result = checkRecoveryInjection("WithoutMoving.before.java",
                "WithoutMoving.after.java",
                "WithoutMoving.java"
        );

        assertTrue(getMessage("testWithoutMoving"), result);
    }

    public void testWithRename() {
        boolean result = checkRecoveryInjection("WithRename.before.java",
                "WithRename.after.java",
                "WithRename.java");

        assertTrue(getMessage("testWithRename"), result);
    }

    private boolean checkRecoveryInjection(String beforeName, String afterName, String baseName) {
        PsiFile[] psiFiles = myFixture.configureByFiles(beforeName, afterName);
        myFixture.renameElement(psiFiles[0], baseName);
        Project project = myFixture.getProject();
        Editor editor = myFixture.getEditor();
        assertFalse(isInjected(project, editor, psiFiles[0]));

        TestHelper.injectLanguage(project, editor, psiFiles[0]);
        assertTrue(isInjected(project, editor, psiFiles[0]));

        myFixture.renameElement(psiFiles[0], beforeName);
        myFixture.renameElement(psiFiles[1], baseName);

        PersistentStorage.getInstance(project).getState();
        new InitializerHighlightListener(project).fileOpened(FileEditorManager.getInstance(project), psiFiles[1].getVirtualFile());
        myFixture.openFileInEditor(psiFiles[1].getVirtualFile());

        return isInjected(project, editor, psiFiles[1]);
    }

    private boolean isInjected(Project project, @NotNull Editor editor, PsiFile psiFile) {
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, editor.getCaretModel().getOffset());
        return element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());
    }

    private String getMessage(String testName) {
        return String.format("\nTest name: %s\nMessage: Injection for literal must persist after modification in another editor\n", testName);
    }
}
