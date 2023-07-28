package org.rri.ijTextmate.Storage.TemporaryStorage;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPsiElementPointer;
import org.rri.ijTextmate.Storage.Interfaces.LanguageID;

import java.util.Objects;

public class TemporaryPlaceInjection implements LanguageID {
    public SmartPsiElementPointer<PsiLanguageInjectionHost> hostPointer;
    public final String languageID;

    public TemporaryPlaceInjection(SmartPsiElementPointer<PsiLanguageInjectionHost> hostPointer, final String languageID) {
        this.hostPointer = hostPointer;
        this.languageID = languageID;
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
}
