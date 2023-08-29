package org.rri.ijTextmate.Helpers.LanguageInformationExtractor;

import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class ExtractedLanguageInformation {
    private final Icon icon;
    private final List<String> keywords;

    public ExtractedLanguageInformation() {
        this.icon = AllIcons.Actions.Words;
        this.keywords = Collections.emptyList();
    }

    public ExtractedLanguageInformation(Icon icon, List<String> keywords) {
        this.icon = getValueOrDefault(icon, AllIcons.Actions.Words);
        this.keywords = getValueOrDefault(keywords, Collections.emptyList());
    }

    public Icon getIcon() {
        return icon;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    private static <T> T getValueOrDefault(@Nullable T value, @NotNull T def) {
        return value != null ? value : def;
    }
}
