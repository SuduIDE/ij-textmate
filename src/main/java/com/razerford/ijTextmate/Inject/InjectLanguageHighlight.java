package com.razerford.ijTextmate.Inject;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.razerford.ijTextmate.PersistentStorage.MyTemporaryLanguageInjectionSupport;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateLanguage;

import java.util.List;

public class InjectLanguageHighlight implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!(context instanceof PsiLiteralValue && context instanceof PsiLanguageInjectionHost host)) return;
        InjectedLanguage language = host.getUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE);
        if (language == null) {
            PsiElement element = host.getOriginalElement();
            language = findLanguageRoot(element);
        }
        if (language == null) return;
        int start = 0;
        int end = host.getTextLength() - 1;
        String text = host.getText();
        while (text.charAt(start) == '"' && start < end) start++;
        while (text.charAt(end) == '"' && end > start) end--;
        TextRange range = new TextRange(start, end + 1);
        registrar.startInjecting(TextMateLanguage.LANGUAGE, language.getSuffix()).addPlace(null, null, host, range).doneInjecting();
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLiteralValue.class);
    }

    public InjectedLanguage findLanguageRoot(PsiElement element) {
        if (element == null) return null;
        PsiReference psiReference = InjectLanguage.getFirstReference(element.getParent());
        if (psiReference == null) return null;
        element = psiReference.resolve();
        element = InjectLanguage.getHostFromElementRoot(element);
        return (element == null) ? null :
                element.getUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE);
    }
}