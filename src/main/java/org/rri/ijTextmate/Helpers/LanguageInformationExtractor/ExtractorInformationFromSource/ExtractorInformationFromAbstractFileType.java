package org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractorInformationFromSource;

import com.intellij.ide.highlighter.custom.SyntaxTable;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractedLanguageInformation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtractorInformationFromAbstractFileType implements ExtractorInformationFromSource {
    private final AbstractFileType abstractFileType;

    public ExtractorInformationFromAbstractFileType(AbstractFileType abstractFileType) {
        this.abstractFileType = abstractFileType;
    }

    @Override
    public ExtractedLanguageInformation extract() {
        return new ExtractedLanguageInformation(abstractFileType.getIcon(), extractKeywordsFromAbstractLanguage(abstractFileType));
    }

    private @NotNull List<String> extractKeywordsFromAbstractLanguage(@NotNull AbstractFileType abstractFileType) {
        SyntaxTable syntaxTable = abstractFileType.getSyntaxTable();
        Set<String> merged = new HashSet<>() {
            {
                addAll(syntaxTable.getKeywords1());
                addAll(syntaxTable.getKeywords2());
                addAll(syntaxTable.getKeywords3());
                addAll(syntaxTable.getKeywords4());
            }
        };
        return merged.stream().toList();
    }
}
