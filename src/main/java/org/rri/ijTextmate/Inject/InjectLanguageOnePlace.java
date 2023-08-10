package org.rri.ijTextmate.Inject;

import com.intellij.psi.PsiLanguageInjectionHost;
import org.rri.ijTextmate.Helpers.InjectorHelper;

public class InjectLanguageOnePlace extends AbstractInjectLanguage {
    public static InjectLanguageOnePlace INSTANCE = new InjectLanguageOnePlace();

    public String getIdentifier() {
        return "Inject one place";
    }

    @Override
    public PsiLanguageInjectionHost getHost(PsiLanguageInjectionHost host) {
        host = InjectorHelper.resolveHost(host);
        if (!host.isValidHost()) return null;
        return host;
    }
}
