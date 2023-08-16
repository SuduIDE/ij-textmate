package org.rri.ijTextmate;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class CompletionInjectedLanguageTests extends LightPlatformCodeInsightFixture4TestCase {
    private static final Set<String> dockerKeywords = Set.of("ADD", "COPY", "ENV", "EXPOSE", "FROM", "LABEL", "STOPSIGNAL", "USER", "VOLUME", "WORKDIR", "ONBUILD", "CMD");
    private static final Set<String> cppKeywords = Set.of("int", "if", "#ifdef", "#ifndef", "void", "virtual");

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/CompletionCases";
    }

    @Test
    public void testSQLSELECTKeyword() {
        Project project = myFixture.getProject();
        PsiFile file = myFixture.configureByFile("SQLSELECTKeyword.java");
        Editor editor = myFixture.getEditor();

        TestHelper.injectLanguage(project, editor, file);
        assertTrue(TestHelper.isInjected(project, editor, file));

        myFixture.complete(CompletionType.BASIC);
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();

        assertNotNull(lookupElementStrings);
        assertContainsElements(lookupElementStrings, "SELECT");
    }

    @Test
    public void testSQLFROMKeyword() {
        Project project = myFixture.getProject();
        PsiFile file = myFixture.configureByFile("SQLFROMKeyword.java");
        Editor editor = myFixture.getEditor();

        TestHelper.injectLanguage(project, editor, file);
        assertTrue(TestHelper.isInjected(project, editor, file));

        myFixture.complete(CompletionType.BASIC);
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();

        assertNotNull(lookupElementStrings);
        Assert.assertThat(lookupElementStrings, RegexMatcher.matchesRegex("FROM"));
    }

    @Test
    public void testCSSEchoKeyword() {
        Project project = myFixture.getProject();
        PsiFile file = myFixture.configureByFile("CSSEchoKeyword.java");
        Editor editor = myFixture.getEditor();

        TestHelper.injectLanguage(project, editor, file, "powershell");
        assertTrue(TestHelper.isInjected(project, editor, file));

        myFixture.complete(CompletionType.BASIC);
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();

        assertNotNull(lookupElementStrings);
        Assert.assertThat(lookupElementStrings, RegexMatcher.matchesRegex("echo"));
    }

    @Test
    public void testDockerKeywords() {
        Project project = myFixture.getProject();
        PsiFile file = myFixture.configureByFile("DockerKeywords.java");
        Editor editor = myFixture.getEditor();

        TestHelper.injectLanguage(project, editor, file, "docker");
        assertTrue(TestHelper.isInjected(project, editor, file));

        myFixture.complete(CompletionType.BASIC);
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();

        assertNotNull(lookupElementStrings);
        for (String keyword : dockerKeywords) {
            assertTrue(lookupElementStrings.contains(keyword));
        }
    }

    @Test
    public void testCppKeywords() {
        Project project = myFixture.getProject();
        PsiFile file = myFixture.configureByFile("CppKeywords.java");
        Editor editor = myFixture.getEditor();

        TestHelper.injectLanguage(project, editor, file, "cpp");
        assertTrue(TestHelper.isInjected(project, editor, file));

        myFixture.complete(CompletionType.BASIC);
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();

        assertNotNull(lookupElementStrings);
        for (String keyword : cppKeywords) {
            assertTrue(lookupElementStrings.contains(keyword));
        }
    }

    public static class RegexMatcher extends TypeSafeMatcher<List<String>> {
        private final String search;

        public RegexMatcher(String word) {
            this.search = word;
        }

        @Override
        protected boolean matchesSafely(@NotNull List<String> strings) {
            AtomicBoolean result = new AtomicBoolean(true);
            strings.forEach(word -> result.set(result.get() && word.contains(search)));
            return result.get();
        }

        @Override
        public void describeTo(@NotNull Description description) {
            description.appendText(String.format("Any word must contain a substring: `%s`", search));
        }

        @Contract("_ -> new")
        public static @NotNull RegexMatcher matchesRegex(final String search) {
            return new RegexMatcher(search);
        }
    }
}