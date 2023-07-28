package org.rri.ijTextmate;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateLanguage;
import org.rri.ijTextmate.Helpers.TextMateHelper;
import org.rri.ijTextmate.Storage.Interfaces.LanguageID;

import java.util.List;

public class LanguageHighlight implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!(context instanceof PsiLiteralValue && context instanceof PsiLanguageInjectionHost host)) return;
        LanguageID languageID = host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

        if (languageID == null) {
            PsiElement element = host.getOriginalElement();
            languageID = findLanguageRoot(element);
        }

        if (languageID == null) return;

        int start = 0;
        int end = host.getTextLength() - 1;
        String text = host.getText();
        while (text.charAt(start) == '"' && start < end) start++;
        while (text.charAt(end) == '"' && end > start) end--;
        TextRange range = new TextRange(start, end + 1);
        String fileExtension = TextMateHelper.getInstance(context.getProject()).getExtension(languageID.getID());
        registrar.startInjecting(TextMateLanguage.LANGUAGE, fileExtension).addPlace(null, null, host, range).doneInjecting();
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLiteralValue.class);
    }

    public LanguageID findLanguageRoot(PsiElement element) {
        if (element == null) return null;
        PsiReference psiReference = InjectorHelper.getFirstReference(element.getParent());
        if (psiReference == null) return null;
        element = psiReference.resolve();
        element = InjectorHelper.getHostFromElementRoot(element);
        return (element == null) ? null :
                element.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);
    }
}