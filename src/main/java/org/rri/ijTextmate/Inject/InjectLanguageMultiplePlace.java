package org.rri.ijTextmate.Inject;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

public class InjectLanguageMultiplePlace extends AbstractInjectLanguage {
    public static InjectLanguageMultiplePlace INSTANCE = new InjectLanguageMultiplePlace();

    public String getIdentifier() {
        return "Inject where used";
    }

    @Override
    public TemporaryPlaceInjection getTemporaryPlaceInjection(@NotNull PsiLanguageInjectionHost host, String languageID) {
        host = InjectorHelper.resolveHost(host);
        if (!host.isValidHost()) return null;
        SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer = SmartPointerManager.createPointer(host);
        return new TemporaryPlaceInjection(psiElementPointer, languageID);
    }
}
