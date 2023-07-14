package com.razerford.ijTextmate.Inject;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InjectorUtils {
    @Nullable
    public static PsiLanguageInjectionHost findInjectionHost(@NotNull Editor editor, @NotNull PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        FileViewProvider vp = file.getViewProvider();
        for (Language language : vp.getLanguages()) {
            PsiLanguageInjectionHost host = PsiTreeUtil.getParentOfType(
                    vp.findElementAt(offset, language),
                    PsiLanguageInjectionHost.class, false);
            if (host != null && host.isValidHost()) return host;
        }
        return null;
    }
}
