package org.rri.ijTextmate;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.util.FileContentUtil;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InjectionLanguageAfterRebuildingPsiTreeTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    public void testInjectionLanguageAfterRebuildingPsiTree() {
        checkInjectionLanguageAfterRebuildingPsiTree("CaretInTheCenterInsideTheString.java");
        checkInjectionLanguageAfterRebuildingPsiTree("CaretInTheCenterInsideTheString.py");
    }

    public void checkInjectionLanguageAfterRebuildingPsiTree(String fileName) {
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = myFixture.getEditor();
        Project project = myFixture.getProject();

        TestHelper.checkWithConsumer(TestCase::assertNotNull, psiFile, editor, project);
        TestHelper.injectLanguage(project, editor, psiFile, myFixture.getTestRootDisposable());
        assertTrue(getMessage(fileName), isInjected(project, editor, psiFile));

        FileContentUtil.reparseFiles(project, List.of(psiFile.getVirtualFile()), true);
        psiFile = myFixture.getFile();
        editor = myFixture.getEditor();
        project = myFixture.getProject();

        TestHelper.checkWithConsumer(TestCase::assertNotNull, psiFile, editor, project);
        assertTrue(getMessage(fileName), isInjected(project, editor, psiFile));
    }

    private boolean isInjected(Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        psiFile.getManager().dropPsiCaches();
        PsiElement element = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, editor.getCaretModel().getOffset());
        return element != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(element.getContainingFile());
    }

    private String getMessage(String fileName) {
        String MESSAGE = "after rebuilding the Psi tree, the internal language disappeared";
        return String.format("\nFile: %s\nMessage: %s\n", fileName, MESSAGE);
    }
}
