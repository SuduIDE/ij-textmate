package org.rri.ijTextmate;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.util.FileContentUtil;
import org.junit.Test;

import java.util.List;

public class InjectionLanguageAfterRebuildingPsiTreeTest extends LightPlatformCodeInsightFixture4TestCase {
    private static final String JAVA_FILE = "InjectionLanguageAfterRebuildingPsiTreeTestCase.java";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void testInjectionLanguageAfterRebuildingPsiTree() {
        checkInjectionLanguageAfterRebuildingPsiTree(JAVA_FILE);
    }

    public void checkInjectionLanguageAfterRebuildingPsiTree(String fileName) {
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = myFixture.getEditor();
        Project project = myFixture.getProject();

        TestHelper.injectLanguage(project, editor, psiFile);

        FileContentUtil.reparseFiles(project, List.of(psiFile.getVirtualFile()), true);

        psiFile = myFixture.getFile();
        editor = myFixture.getEditor();
        project = myFixture.getProject();

        assertTrue(getMessage(fileName), TestHelper.isInjected(project, editor, psiFile));
    }

    private String getMessage(String fileName) {
        String message = "after rebuilding the psi tree, the internal language disappeared";
        return String.format("\nFile: %s\nMessage: %s\n", fileName, message);
    }
}
