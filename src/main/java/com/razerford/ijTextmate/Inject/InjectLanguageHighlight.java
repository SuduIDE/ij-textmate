package com.razerford.ijTextmate.Inject;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.razerford.ijTextmate.TemporaryEntity.MyTemporaryLanguageInjectionSupport;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateLanguage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InjectLanguageHighlight implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!(context instanceof PsiLiteralValue && context instanceof PsiLanguageInjectionHost host)) return;
        InjectedLanguage language = host.getUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE);
        if (language == null) return;
        PsiElement element = host.getOriginalElement();
        if (!(element instanceof PsiLiteralValue)) return;
        ArrayList<PsiLanguageInjectionHost> hosts = new ArrayList<>(List.of(host));
        if (element.getParent() != null) {
            element = element.getParent();
            if (!(element instanceof PsiNameIdentifierOwner) && element.getFirstChild() instanceof PsiReference psiReference) {
                element = psiReference.resolve();
            }
            Collection<PsiReference> references = new ArrayList<>();
            if (element != null) {
                hosts.add(getHostFromElementRoot(element));
                references = ReferencesSearch.search(element).findAll();
            }
            for (PsiReference reference : references) {
                if (reference.getElement().getParent().getLastChild() instanceof PsiLanguageInjectionHost hostAdd) {
                    hosts.add(hostAdd);
                }
            }
        }
        registrar = registrar.startInjecting(TextMateLanguage.LANGUAGE, language.getSuffix());
        for (PsiLanguageInjectionHost hostAdd : hosts) {
            int start = 0;
            int end = hostAdd.getTextLength() - 1;
            String text = hostAdd.getText();
            while (text.charAt(start) == '"' && start < end) start++;
            while (text.charAt(end) == '"' && end > start) end--;
            TextRange range = new TextRange(start, end + 1);
            registrar.addPlace(null, null, hostAdd, range);
        }
        registrar.doneInjecting();
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLiteralValue.class);
    }

    @Contract(pure = true)
    private PsiLanguageInjectionHost getHostFromElementRoot(@NotNull PsiElement root) {
        for (PsiElement element : root.getChildren()) {
            if (element instanceof PsiLanguageInjectionHost host) {
                return host;
            }
        }
        throw new IllegalArgumentException();
    }
}