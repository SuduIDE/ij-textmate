package org.rri.ijTextmate;

import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Inject.InjectLanguage;

public class UnInjectionAvailabilityTests extends LightJavaCodeInsightFixtureTestCase {
    private final static String INJECTED_LANGUAGE = "sql";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionAvailabilityCases";
    }

    @Test
    public void testCaretInTheCenterInsideTheString() {
        checkUnInjectionAvailability("CaretInTheCenterInsideTheString.java", caretInsideString);
    }

    @Test
    public void testCaretOnTheLeftInsideTheString() {
        checkUnInjectionAvailability("CaretOnTheLeftInsideTheString.java", caretInsideString);
    }

    @Test
    public void testCaretOnTheRightInsideTheString() {
        checkUnInjectionAvailability("CaretOnTheRightInsideTheString.java", caretInsideString);
    }

    @Test
    public void testCaretOnTheLeftOutsideOfTheString() {
        checkUnInjectionAvailability("CaretOnTheLeftOutsideOfTheString.java", caretOutsideString);
    }

    @Test
    public void testCaretOnTheRightOutsideOfTheString() {
        checkUnInjectionAvailability("CaretOnTheRightOutsideOfTheString.java", caretOutsideString);
    }

    @Test
    public void testSelectionInsideTheString() {
        PsiFile psiFile = myFixture.configureByFile("SelectionInsideTheString.java");
        Editor editor = getEditor();
        Project project = getProject();
        checkNotNull(project, psiFile, editor);
        for (CaretState caretState : editor.getCaretModel().getCaretsAndSelections()) {
            checkNotNull(caretState.getSelectionStart(), caretState.getSelectionEnd());
            int i = caretState.getSelectionStart().column;
            int end = caretState.getSelectionEnd().column;
            int line = caretState.getSelectionStart().line;
            do {
                editor.getCaretModel().moveToVisualPosition(new VisualPosition(line, i));
                checkNotNull(project, psiFile, editor);
                caretInsideString.check(project, psiFile, editor);
            } while (++i < end);
        }
    }

    @Contract(pure = true)
    private void checkNotNull(Object @NotNull ... args) {
        for (Object arg : args) {
            assertNotNull(arg);
        }
    }

    private void checkUnInjectionAvailability(final String fileName, @NotNull CheckWithInjectedLanguage checker) {
        Project project = getProject();
        PsiFile psiFile = myFixture.configureByFile(fileName);
        Editor editor = getEditor();
        checkNotNull(project, psiFile, editor);
        checker.check(project, psiFile, editor);
    }

    private boolean canUnInjectLanguageToHost(Project project, PsiFile psiFile, Editor editor) {
        PsiLanguageInjectionHost host = getHost(editor, psiFile);
        UnInjectLanguageAction action = new UnInjectLanguageAction();
        return action.canUnInjectLanguageToHost(project, editor, psiFile, host);
    }

    private void injectLanguage(Project project, Editor editor, PsiFile psiFile) {
        PsiLanguageInjectionHost host = getHost(editor, psiFile);
        if (host == null) return;
        InjectLanguage.inject(host, InjectedLanguage.create(INJECTED_LANGUAGE), project);
    }

    private PsiLanguageInjectionHost getHost(Editor editor, PsiFile psiFile) {
        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, psiFile);
        host = InjectorHelper.resolveHost(host);
        return host;
    }

    private final CheckWithInjectedLanguage caretInsideString = (Project project, PsiFile psiFile, Editor editor) -> {
        injectLanguage(project, editor, psiFile);
        assertTrue(canUnInjectLanguageToHost(project, psiFile, editor));
    };

    private final CheckWithInjectedLanguage caretOutsideString = (Project project, PsiFile psiFile, Editor editor) -> {
        assertFalse(canUnInjectLanguageToHost(project, psiFile, editor));
    };

    @FunctionalInterface
    private interface CheckWithInjectedLanguage {
        void check(Project project, PsiFile psiFile, Editor editor);
    }
}
