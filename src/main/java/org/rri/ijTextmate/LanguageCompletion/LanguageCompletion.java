package org.rri.ijTextmate.LanguageCompletion;

import com.intellij.codeInsight.completion.*;
import org.jetbrains.plugins.textmate.TextMateLanguage;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;

public class LanguageCompletion extends CompletionContributor {
    public LanguageCompletion() {
        extend(CompletionType.BASIC,
                psiElement().inFile(psiFile().withLanguage(TextMateLanguage.LANGUAGE)).
                        with(CompletionPattern.INSTANCE),
                KeywordCompletionProvider.INSTANCE);
    }
}
