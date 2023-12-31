package org.rri.ijTextmate.LanguageCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractedLanguageInformation;
import org.rri.ijTextmate.Helpers.TextMateHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

public class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {
    public static final KeywordCompletionProvider INSTANCE = new KeywordCompletionProvider();

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        TextMateHelper textMateHelper = (TextMateHelper) context.get(CompletionPattern.LANGUAGE);

        TemporaryPlaceInjection languageID = parameters.getOriginalFile().getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

        if (languageID == null) return;

        ExtractedLanguageInformation information = textMateHelper.getInformation(languageID.getID());

        information.getKeywords().forEach(word -> {
            ProgressManager.checkCanceled();
            result.addElement(LookupElementBuilder.create(word).withCaseSensitivity(false).withIcon(information.getIcon()));
        });
    }
}
