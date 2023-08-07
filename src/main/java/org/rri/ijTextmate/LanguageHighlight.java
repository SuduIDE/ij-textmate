package org.rri.ijTextmate;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateLanguage;
import org.rri.ijTextmate.Helpers.TextMateHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.List;
import java.util.Map;

public class LanguageHighlight implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!(context instanceof PsiLanguageInjectionHost host) || !host.isValidHost()) return;

        TemporaryPlaceInjection languageID = getTemporaryPlaceInjection(host);
        if (languageID == null) return;

        int start = 0;
        int end = host.getTextLength() - 1;
        String text = host.getText();
        while (text.charAt(start) == '"' && start < end) start++;
        while (text.charAt(end) == '"' && end > start) end--;
        int count = Math.min(start, host.getTextLength() - end);

        TextRange range = new TextRange(count, host.getTextLength() - count);
        String fileExtension = TextMateHelper.getInstance(context.getProject()).getExtension(languageID.getID());
        registrar.startInjecting(TextMateLanguage.LANGUAGE, fileExtension).addPlace(null, null, host, range).doneInjecting();

        @SuppressWarnings("deprecation")
        PsiFile psiFile = InjectedLanguageUtil.getCachedInjectedFileWithLanguage(host, TextMateLanguage.LANGUAGE);
        if (psiFile == null) return;
        psiFile.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, languageID);
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLanguageInjectionHost.class);
    }

    public TemporaryPlaceInjection findLanguageRoot(PsiElement element) {
        if (element == null) return null;
        PsiReference psiReference = InjectorHelper.getFirstReference(element.getParent());
        if (psiReference == null) return null;
        element = psiReference.resolve();
        element = InjectorHelper.getHostFromElementRoot(element);
        return (element == null) ? null : element.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);
    }

    private TemporaryPlaceInjection getTemporaryPlaceInjection(@NotNull PsiLanguageInjectionHost host) {
        TemporaryPlaceInjection languageID = host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

        if (languageID != null) return languageID;

        PsiElement element = host.getOriginalElement();
        languageID = findLanguageRoot(element);

        if (languageID != null) return languageID;

        Project project = host.getProject();
        PsiFile psiFile = host.getContainingFile();

        Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, String> map = TemporaryStorage
                .getInstance(project)
                .get(InjectorHelper.gitRelativePath(project, psiFile))
                .getMap();

        host = InjectorHelper.resolveHost(host);

        for (var entry : map.entrySet()) {
            element = entry.getKey().getElement();
            if (element != null && element.getTextRange().intersects(host.getTextRange())) {
                return new TemporaryPlaceInjection(entry.getKey(), entry.getValue());
            }
        }

        return languageID;
    }
}