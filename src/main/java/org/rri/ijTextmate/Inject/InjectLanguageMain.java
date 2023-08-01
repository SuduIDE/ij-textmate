package org.rri.ijTextmate.Inject;

import com.intellij.psi.*;
import org.rri.ijTextmate.Helpers.InjectorHelper;

public class InjectLanguageMain extends AbstractInjectLanguage {
    public static InjectLanguageMain INSTANCE = new InjectLanguageMain();

    public PsiLanguageInjectionHost getHost(PsiLanguageInjectionHost host) {
        host = InjectorHelper.resolveHost(host);
        if (!host.isValidHost()) return null;
        return host;
    }
}
