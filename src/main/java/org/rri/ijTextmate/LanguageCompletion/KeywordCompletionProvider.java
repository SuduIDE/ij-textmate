package org.rri.ijTextmate.LanguageCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.TextMateHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

public class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {
    public static final KeywordCompletionProvider INSTANCE = new KeywordCompletionProvider();

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.runRemainingContributors(parameters, true);
        TextMateHelper textMateHelper = (TextMateHelper) context.get(CompletionPattern.LANGUAGE);

        TemporaryPlaceInjection languageID = parameters.getOriginalFile().getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

        if (languageID == null) return;

        textMateHelper.getKeywords(languageID.getID()).forEach(word -> {
            ProgressManager.checkCanceled();
            result.addElement(LookupElementBuilder.create(word));
        });
//        if (parameters.isAutoPopup()) return;
//
//        @SuppressWarnings("unchecked") List<String> list = (List<String>) context.get(CompletionPattern.LANGUAGE);
//
//        String prefix = result.getPrefixMatcher().getPrefix();
//
//        if (prefix.isEmpty()) return;
//
//        result.runRemainingContributors(parameters, true);
//
//        CompletionResultSet newResult;
//
//        int lastSpace = prefix.lastIndexOf(' ');
//
//        if (lastSpace >= 0 && lastSpace < prefix.length() - 1) {
//            prefix = prefix.substring(lastSpace + 1);
//            newResult = result.withPrefixMatcher(prefix);
//        } else {
//            newResult = result;
//        }
//
//        list.forEach((word) -> {
//            ProgressManager.checkCanceled();
//            newResult.addElement(LookupElementBuilder.create(word));
//        });
//        result.stopHere();
    }
}