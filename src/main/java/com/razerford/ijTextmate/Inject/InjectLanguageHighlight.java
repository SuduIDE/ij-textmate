package com.razerford.ijTextmate.Inject;

import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralValue;
import com.razerford.ijTextmate.InjectableTextMate;
import com.razerford.ijTextmate.TemporaryEntity.MyTemporaryLanguageInjectionSupport;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InjectLanguageHighlight implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!(context instanceof PsiLiteralValue && context instanceof PsiLanguageInjectionHost host)) return;

//        InjectedLanguage language = host.getUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE);

//        if (language == null || language.getLanguage() == null) return;
        InjectedLanguage language = InjectedLanguage.create("textmate");
        int start = 0;
        int end = context.getTextLength() - 1;
        String text = context.getText();
        while (text.charAt(start) == '"' && start < end) start++;
        while (text.charAt(end) == '"' && end > start) end--;
        TextRange range = new TextRange(start, end);
        registrar.startInjecting(language.getLanguage(), "sql").addPlace(null, null, host, range).doneInjecting();
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLiteralValue.class);
    }
}