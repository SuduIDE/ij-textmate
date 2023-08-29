package org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

import java.util.Collection;
import java.util.List;

public class RootMultipleInjectionStrategy implements InjectionStrategy {
    private final InjectionStrategy single = new SingleInjectionStrategy();

    @Override
    public String identifier() {
        return "RootMultipleInjectionStrategy";
    }

    @Override
    public void register(@NotNull MultiHostRegistrar registrar,
                         @NotNull PsiLanguageInjectionHost host,
                         @NotNull List<TextRange> ranges,
                         @NotNull TemporaryPlaceInjection languageID) {
        single.register(registrar, host, ranges, languageID);
    }

    @Override
    public void delete(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        single.delete(temporaryPlaceInjection);

        PsiElement psiElement = temporaryPlaceInjection.hostPointer.getElement();
        if (psiElement == null) return;

        psiElement = psiElement.getParent();

        if (!(psiElement instanceof PsiNamedElement)) {
            psiElement = PsiTreeUtil.findChildOfAnyType(psiElement, PsiNamedElement.class);
        }

        if (psiElement == null) return;

        Collection<PsiReference> references = ReferencesSearch.search(psiElement).findAll();

        for (PsiReference reference : references) {
            PsiLanguageInjectionHost hostAdd = PsiTreeUtil.getChildOfType(reference.getElement().getParent(), PsiLanguageInjectionHost.class);

            if (hostAdd == null) continue;
            TemporaryPlaceInjection placeInjection = hostAdd.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

            if (placeInjection == null) continue;
            single.delete(placeInjection);
        }
    }
}
