package org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

public class LeafMultipleInjectionStrategy implements InjectionStrategy {
    private final InjectionStrategy single = new SingleInjectionStrategy();
    private final TemporaryPlaceInjection temporaryPlaceInjectionRoot;

    public LeafMultipleInjectionStrategy(TemporaryPlaceInjection temporaryPlaceInjectionRoot) {
        this.temporaryPlaceInjectionRoot = temporaryPlaceInjectionRoot;
    }

    @Override
    public String identifier() {
        return "LeafMultipleInjectionStrategy";
    }

    @Override
    public void register(@NotNull MultiHostRegistrar registrar, @NotNull PsiLanguageInjectionHost host, @NotNull TextRange range, @NotNull TemporaryPlaceInjection languageID) {
        single.register(registrar, host, range, languageID);
    }

    @Override
    public void delete(@NotNull TemporaryPlaceInjection ignored) {
        temporaryPlaceInjectionRoot.delete();
    }
}
