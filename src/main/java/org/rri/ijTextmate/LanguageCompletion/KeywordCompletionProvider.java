package org.rri.ijTextmate.LanguageCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {
    public static final KeywordCompletionProvider INSTANCE = new KeywordCompletionProvider();

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        if (parameters.isAutoPopup()) return;

        @SuppressWarnings("unchecked") List<String> list = (List<String>) context.get(CompletionPattern.LANGUAGE);

        String prefix = result.getPrefixMatcher().getPrefix();

        if (prefix.isEmpty()) return;

        result.runRemainingContributors(parameters, true);

        CompletionResultSet newResult;

        int lastSpace = prefix.lastIndexOf(' ');

        if (lastSpace >= 0 && lastSpace < prefix.length() - 1) {
            prefix = prefix.substring(lastSpace + 1);
            newResult = result.withPrefixMatcher(prefix);
        } else {
            newResult = result;
        }

        list.forEach((word) -> {
            ProgressManager.checkCanceled();
            newResult.addElement(LookupElementBuilder.create(word));
        });
        result.stopHere();
    }
}
