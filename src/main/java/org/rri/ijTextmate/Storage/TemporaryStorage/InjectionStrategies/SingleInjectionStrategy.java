package org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateLanguage;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Helpers.TextMateHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.List;

public class SingleInjectionStrategy implements InjectionStrategy {
    @Override
    public String identifier() {
        return "SingleInjectionStrategy";
    }

    @Override
    public void register(@NotNull MultiHostRegistrar registrar,
                         @NotNull PsiLanguageInjectionHost host,
                         @NotNull List<TextRange> ranges,
                         @NotNull TemporaryPlaceInjection languageID) {
        host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, languageID);
        String fileExtension = TextMateHelper.getInstance(host.getProject()).getExtension(languageID.getID());
        registrar.startInjecting(TextMateLanguage.LANGUAGE, fileExtension);

        for (TextRange range : ranges) {
            registrar.addPlace(null, null, host, range);
        }

        registrar.doneInjecting();

        @SuppressWarnings("deprecation")
        PsiFile psiFile = InjectedLanguageUtil.getCachedInjectedFileWithLanguage(host, TextMateLanguage.LANGUAGE);
        if (psiFile == null) return;
        psiFile.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, languageID);
    }

    @Override
    public void delete(@NotNull TemporaryPlaceInjection temporaryPlaceInjection) {
        PsiElement psiElement = temporaryPlaceInjection.hostPointer.getElement();
        if (psiElement == null || !psiElement.isValid()) return;

        psiElement.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, null);

        Project project = psiElement.getProject();
        PsiFile psiFile = psiElement.getContainingFile();

        String relativePath = InjectorHelper.getRelativePath(project, psiFile);
        TemporaryMapPointerToLanguage mapPointerToLanguage = TemporaryStorage.getInstance(project).get(relativePath);
        mapPointerToLanguage.remove(temporaryPlaceInjection);

        psiElement.getManager().dropPsiCaches();
    }
}
