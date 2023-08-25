package org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractorInformationFromSource;

import com.intellij.ide.highlighter.custom.SyntaxTable;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractedLanguageInformation;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.util.List;
import java.util.Map;

public interface ExtractorInformationFromSource {
    ExtractedLanguageInformation extract();

    static @NotNull ExtractorInformationFromSource create(@NotNull List<String> extensions, @NotNull String language, Map<String, AbstractFileType> extensionToFileType, SelectingRegistersStrategy registersStrategy) {
        String extension = abstractFileTypeExists(extensions, extensionToFileType);
        AbstractFileType abstractFileType = extensionToFileType.get(extension);
        ExtractorInformationFromSource extractor;
        if (abstractFileType != null && !isEmpty(abstractFileType)) {
            extractor = new ExtractorInformationFromAbstractFileType(abstractFileType);
        } else {
            extractor = new ExtractorInformationFromTextMate(language, extensions, registersStrategy);
        }
        return extractor;
    }

    private static @Nullable String abstractFileTypeExists(List<String> extensions, @NotNull Map<String, AbstractFileType> extensionToFileType) {
        for (String extension : extensionToFileType.keySet()) {
            if (extensions.contains(extension)) return extension;
        }
        return null;
    }

    private static boolean isEmpty(@NotNull AbstractFileType abstractFileType) {
        SyntaxTable syntaxTable = abstractFileType.getSyntaxTable();
        return syntaxTable.getKeywords1().isEmpty() &&
                syntaxTable.getKeywords2().isEmpty() &&
                syntaxTable.getKeywords3().isEmpty() &&
                syntaxTable.getKeywords4().isEmpty();
    }
}
