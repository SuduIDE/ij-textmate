package org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateLanguage;
import org.rri.ijTextmate.Constants;
import org.rri.ijTextmate.Helpers.TextMateHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

public class LeafMultipleInjectionStrategy implements InjectionStrategy {
    @Override
    public String identifier() {
        return "LeafMultipleInjectionStrategy";
    }

    @Override
    public void registrar(@NotNull MultiHostRegistrar registrar, @NotNull PsiLanguageInjectionHost host, @NotNull TextRange range, @NotNull TemporaryPlaceInjection languageID) {
        String fileExtension = TextMateHelper.getInstance(host.getProject()).getExtension(languageID.getID());
        registrar.startInjecting(TextMateLanguage.LANGUAGE, fileExtension).addPlace(null, null, host, range).doneInjecting();

        @SuppressWarnings("deprecation")
        PsiFile psiFile = InjectedLanguageUtil.getCachedInjectedFileWithLanguage(host, TextMateLanguage.LANGUAGE);
        if (psiFile == null) return;
        psiFile.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, languageID);
    }

    @Override
    public void delete() {

    }
}
