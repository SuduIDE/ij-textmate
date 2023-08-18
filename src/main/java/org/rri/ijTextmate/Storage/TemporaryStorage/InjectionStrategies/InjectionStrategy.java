package org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

import java.util.List;

public interface InjectionStrategy {
    String identifier();
    void register(@NotNull MultiHostRegistrar registrar, @NotNull PsiLanguageInjectionHost host, @NotNull List<TextRange> ranges, @NotNull TemporaryPlaceInjection languageID);
    void delete(@NotNull TemporaryPlaceInjection temporaryPlaceInjection);
}
