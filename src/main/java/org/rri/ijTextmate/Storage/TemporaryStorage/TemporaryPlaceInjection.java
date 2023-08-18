package org.rri.ijTextmate.Storage.TemporaryStorage;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.Interfaces.LanguageID;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.InjectionStrategy;

import java.util.List;
import java.util.Objects;

public class TemporaryPlaceInjection implements LanguageID {
    public final SmartPsiElementPointer<PsiLanguageInjectionHost> hostPointer;
    public final String languageID;
    private final InjectionStrategy injectionStrategy;

    public TemporaryPlaceInjection(@NotNull SmartPsiElementPointer<PsiLanguageInjectionHost> hostPointer, @NotNull String languageID, @NotNull InjectionStrategy injectionStrategy) {
        this.hostPointer = hostPointer;
        this.languageID = languageID;
        this.injectionStrategy = injectionStrategy;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TemporaryPlaceInjection placeInjection && placeInjection.languageID.equals(languageID) && placeInjection.hostPointer.equals(hostPointer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageID, hostPointer);
    }

    @Override
    public String getID() {
        return languageID;
    }

    public void register(@NotNull MultiHostRegistrar registrar, @NotNull PsiLanguageInjectionHost host, @NotNull List<TextRange> ranges) {
        injectionStrategy.register(registrar, host, ranges, this);
    }

    public void delete() {
        injectionStrategy.delete(this);
    }

    public String getStrategyIdentifier() {
        return injectionStrategy.identifier();
    }

    public InjectionStrategy getInjectionStrategy() {
        return injectionStrategy;
    }
}
