package org.rri.ijTextmate.LanguageCompletion;

import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.TextMateHelper;

public class CompletionPattern extends PatternCondition<PsiElement> {
    public static final CompletionPattern INSTANCE = new CompletionPattern();
    public static final String LANGUAGE = "LANGUAGE";
    public CompletionPattern() {
        super("injectSenseCompletionPattern");
    }

    @Override
    public boolean accepts(@NotNull PsiElement psiElement, @NotNull ProcessingContext context) {
        PsiFile psiFile = psiElement.getContainingFile().getOriginalFile();
        if (psiFile.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE) == null) return false;
        context.put(LANGUAGE, TextMateHelper.getInstance(psiElement.getProject()));
        return true;
    }
}