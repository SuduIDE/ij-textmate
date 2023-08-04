package org.rri.ijTextmate.LanguageCompletion;

import com.intellij.codeInsight.completion.*;
import com.intellij.psi.PsiLanguageInjectionHost;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class LanguageCompletion extends CompletionContributor {
    public LanguageCompletion() {
        extend(CompletionType.BASIC,
                psiElement().withParent(PsiLanguageInjectionHost.class).with(CompletionPattern.INSTANCE),
                KeywordCompletionProvider.INSTANCE);
    }
}
