package org.rri.ijTextmate.UnInject;

import com.intellij.psi.PsiLanguageInjectionHost;
import org.rri.ijTextmate.Helpers.InjectorHelper;

public class UnInjectLanguageMain extends AbstractUnInjectLanguage {
    public static UnInjectLanguageMain INSTANCE = new UnInjectLanguageMain();

    @Override
    public PsiLanguageInjectionHost getHost(PsiLanguageInjectionHost host) {
        host = InjectorHelper.resolveHost(host);
        if (!host.isValidHost()) return null;
        return host;
    }
}
