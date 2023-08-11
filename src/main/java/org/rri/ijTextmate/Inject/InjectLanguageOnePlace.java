package org.rri.ijTextmate.Inject;

import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

public class InjectLanguageOnePlace extends AbstractInjectLanguage {
    public static InjectLanguageOnePlace INSTANCE = new InjectLanguageOnePlace();

    public String getIdentifier() {
        return "Inject one place";
    }

    @Override
    public TemporaryPlaceInjection getTemporaryPlaceInjection(@NotNull PsiLanguageInjectionHost host, String languageID) {
        if (!host.isValidHost()) return null;
        SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer = SmartPointerManager.createPointer(host);
        return new TemporaryPlaceInjection(psiElementPointer, languageID);
    }
}
