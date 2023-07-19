package org.rri.ijTextmate;

import com.intellij.lang.Language;
import com.intellij.psi.injection.Injectable;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.TextMateLanguage;

public class InjectableTextMate extends Injectable {
    public static final InjectableTextMate INSTANCE = new InjectableTextMate();

    private InjectableTextMate() {
    }

    @Override
    public @NotNull String getId() {
        return TextMateLanguage.LANGUAGE.getID();
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return TextMateLanguage.LANGUAGE.getDisplayName();
    }

    @Override
    public @Nullable Language getLanguage() {
        return TextMateLanguage.LANGUAGE;
    }

    public static @NotNull InjectedLanguage create(@NotNull String languageId) {
        String innerId = INSTANCE.getId();
        if (languageId.length() == 0) return InjectedLanguage.create(innerId);
        return InjectedLanguage.create(String.format("%s->%s", innerId, languageId), innerId, languageId, false);
    }
}
