package org.rri.ijTextmate.LanguageCompletion;

import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Helpers.TextMateHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

public class CompletionPattern extends PatternCondition<PsiElement> {
    public static final CompletionPattern INSTANCE = new CompletionPattern();
    public static final String LANGUAGE = "LANGUAGE";

    public CompletionPattern() {
        super("injectSenseCompletionPattern");
    }

    @Override
    public boolean accepts(@NotNull PsiElement psiElement, @NotNull ProcessingContext context) {
        PsiFile psiFile = psiElement.getContainingFile().getOriginalFile();
        if (psiFile.getUserData(Constants.MY_LANGUAGE_INJECTED) == null) return false;
        PsiLanguageInjectionHost host = InjectorHelper.resolveHost(InjectorHelper.findInjectionHost(psiElement.getTextOffset(), psiFile));

        if (host == null) return false;
        TemporaryPlaceInjection temporaryPlaceInjection = host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

        if (temporaryPlaceInjection == null) return false;
        String languageID = temporaryPlaceInjection.getID();

        context.put(LANGUAGE, TextMateHelper.getInstance(host.getProject()).getKeywords(languageID));
        return true;
    }
}