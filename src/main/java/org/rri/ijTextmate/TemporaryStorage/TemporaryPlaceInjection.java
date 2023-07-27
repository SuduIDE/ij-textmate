package org.rri.ijTextmate.TemporaryStorage;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPsiElementPointer;

import java.util.Objects;

public class TemporaryPlaceInjection {
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
}
