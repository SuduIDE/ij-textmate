package com.razerford.ijTextmate.Helpers;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.razerford.ijTextmate.Constants;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InjectorHelper {
    @Nullable
    public static PsiLanguageInjectionHost findInjectionHost(@NotNull Editor editor, @NotNull PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        return findInjectionHost(offset, file);
    }

    @Nullable
    public static PsiLanguageInjectionHost findInjectionHost(final int offset, @NotNull PsiFile file) {
        FileViewProvider viewProvider = file.getViewProvider();
        for (Language language : viewProvider.getLanguages()) {
            PsiLanguageInjectionHost host = PsiTreeUtil.getParentOfType(
                    viewProvider.findElementAt(offset, language),
                    PsiLanguageInjectionHost.class, false);
            if (host != null && host.isValidHost()) return host;
        }
        return null;
    }

    public static void resolveInjectLanguage(PsiLanguageInjectionHost host, InjectedLanguage language) {
        if (host == null) return;
        PsiElement element = host.getOriginalElement();
        if (element != null) element = element.getParent();
        PsiReference psiReference = InjectorHelper.getFirstReference(element);
        if (!(element instanceof PsiNameIdentifierOwner) && psiReference != null) {
            element = psiReference.resolve();
            PsiLanguageInjectionHost newHost = InjectorHelper.getHostFromElementRoot(element);
            host = (newHost == null) ? host : newHost;
        }
        if (host.isValidHost()) {
            host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, language);
            host.getManager().dropPsiCaches();
        }
    }

    @Contract(pure = true)
    public static @Nullable PsiLanguageInjectionHost getHostFromElementRoot(PsiElement root) {
        if (root == null) return null;
        for (PsiElement element : root.getChildren()) {
            if (element instanceof PsiLanguageInjectionHost host) {
                return host;
            }
        }
        return null;
    }

    @Contract(pure = true)
    public static @Nullable PsiReference getFirstReference(PsiElement root) {
        if (root == null) return null;
        for (PsiElement element : root.getChildren()) {
            if (element instanceof PsiReference reference) {
                return reference;
            }
        }
        return null;
    }
}
