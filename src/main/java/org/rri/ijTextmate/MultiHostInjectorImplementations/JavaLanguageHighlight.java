package org.rri.ijTextmate.MultiHostInjectorImplementations;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.InjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.LeafMultipleInjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.RootMultipleInjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

import java.util.*;

public class JavaLanguageHighlight implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!(context instanceof PsiLanguageInjectionHost host) || !host.isValidHost()) return;

        TemporaryPlaceInjection temporaryPlaceInjection = host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

        if (temporaryPlaceInjection != null) return;
        temporaryPlaceInjection = CachedValuesManager.getCachedValue(host, Constants.MY_CACHED_TEMPORARY_INJECTED_LANGUAGE, new CachedValueProvider<>() {
            @Override
            public @Nullable Result<TemporaryPlaceInjection> compute() {
                PsiElement element = host.getParent();
                PsiReference reference = null;
                for (PsiElement child : element.getChildren()) {
                    if (child instanceof PsiReference newReference) {
                        reference = newReference;
                        break;
                    }
                }
                if (reference == null) return null;

                element = reference.resolve();
                PsiLanguageInjectionHost rootHost = PsiTreeUtil.findChildOfType(element, PsiLanguageInjectionHost.class);
                if (rootHost == null) return null;
                return Result.create(rootHost.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE), PsiModificationTracker.getInstance(host.getProject()));
            }
        });

        if (temporaryPlaceInjection == null || !temporaryPlaceInjection.getStrategyIdentifier().equals(RootMultipleInjectionStrategy.IDENTIFIER)) {
            return;
        }
        SmartPsiElementPointer<PsiLanguageInjectionHost> pointer = SmartPointerManager.createPointer(host);
        String language = temporaryPlaceInjection.languageID;
        InjectionStrategy injectionStrategy = new LeafMultipleInjectionStrategy(temporaryPlaceInjection);

        TemporaryPlaceInjection newTempPlaceInjection = new TemporaryPlaceInjection(pointer, language, injectionStrategy);

        host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, newTempPlaceInjection);

        List<TextRange> ranges = DefaultLanguageHighlight.calculateRanges(host);
        newTempPlaceInjection.register(registrar, host, ranges);
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLanguageInjectionHost.class);
    }
}