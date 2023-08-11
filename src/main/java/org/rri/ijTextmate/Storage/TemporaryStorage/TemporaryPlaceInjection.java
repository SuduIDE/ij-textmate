package org.rri.ijTextmate.Storage.TemporaryStorage;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPsiElementPointer;
import org.rri.ijTextmate.Storage.Interfaces.LanguageID;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.InjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.SingleInjectionStrategy;

import java.util.Objects;

public class TemporaryPlaceInjection implements LanguageID {
    public SmartPsiElementPointer<PsiLanguageInjectionHost> hostPointer;
    public final String languageID;
    private InjectionStrategy injectionStrategy = new SingleInjectionStrategy();

    public TemporaryPlaceInjection(SmartPsiElementPointer<PsiLanguageInjectionHost> hostPointer, final String languageID) {
        this.hostPointer = hostPointer;
        this.languageID = languageID;
    }

    public TemporaryPlaceInjection(SmartPsiElementPointer<PsiLanguageInjectionHost> hostPointer, final String languageID, InjectionStrategy injectionStrategy) {
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

    public void registrar() {
        injectionStrategy.registrar();
    }

    public void delete() {
        injectionStrategy.delete();
    }

    public String getStrategyIdentifier() {
        return injectionStrategy.identifier();
    }
}
