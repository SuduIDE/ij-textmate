package org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractorInformationFromSource;

import com.intellij.icons.AllIcons;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.Constants;
import org.jetbrains.plugins.textmate.TextMateService;
import org.jetbrains.plugins.textmate.language.TextMateLanguageDescriptor;
import org.jetbrains.plugins.textmate.language.syntax.SyntaxNodeDescriptor;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractedLanguageInformation;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.WordExtraction.WordExtractionFactory;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtractorInformationFromTextMate implements ExtractorInformationFromSource {
    private final String language;
    private final List<String> extensions;
    private final SelectingRegistersStrategy registersStrategy;
    private static final Icon icon = AllIcons.Actions.Words;

    public ExtractorInformationFromTextMate(String language, List<String> extensions, SelectingRegistersStrategy registersStrategy) {
        this.language = language;
        this.extensions = extensions;
        this.registersStrategy = registersStrategy;
    }

    @Override
    public ExtractedLanguageInformation extract() {
        return new ExtractedLanguageInformation(icon, extractKeywordsFromTextmateRegex(extensions, language));
    }

    private @NotNull List<String> extractKeywordsFromTextmateRegex(@NotNull List<String> extensions, String language) {
        Set<String> keywords = new HashSet<>();
        Set<String> visited = new HashSet<>();

        for (String extension : extensions) {
            TextMateLanguageDescriptor textMateLanguageDescriptor = TextMateService.getInstance().getLanguageDescriptorByExtension(extension);
            if (textMateLanguageDescriptor == null) continue;

            SyntaxNodeDescriptor nodeDescriptor = textMateLanguageDescriptor.getRootSyntaxNode();
            visited.add(nodeDescriptor.toString());
            recursiveExtractionRegex(nodeDescriptor, keywords, visited, language);
        }
        return extractWordsFromRegexList(language, keywords, registersStrategy);
    }

    private void recursiveExtractionRegex(@NotNull SyntaxNodeDescriptor syntaxNodeDescriptor, @NotNull Set<String> keywords, Set<String> visited, final String language) {
        for (SyntaxNodeDescriptor nodeDescriptor : syntaxNodeDescriptor.getChildren()) {
            if (visited.contains(nodeDescriptor.toString())) continue;
            visited.add(nodeDescriptor.toString());

            recursiveExtractionRegex(nodeDescriptor, keywords, visited, language);

            extractFromNode(nodeDescriptor, keywords, language.toLowerCase());
        }
    }

    private void extractFromNode(@NotNull SyntaxNodeDescriptor nodeDescriptor, @NotNull Set<String> keywords, final String language) {
        String name = tryAdd(nodeDescriptor, language);
        if (name == null) return;

        CharSequence variable = nodeDescriptor.getStringAttribute(Constants.StringKey.MATCH);
        if (variable != null) keywords.add(variable.toString());

        variable = nodeDescriptor.getStringAttribute(Constants.StringKey.BEGIN);
        if (variable != null) keywords.add(variable.toString());

        variable = nodeDescriptor.getStringAttribute(Constants.StringKey.END);
        if (variable != null) keywords.add(variable.toString());
    }

    private String tryAdd(@NotNull SyntaxNodeDescriptor nodeDescriptor, final String language) {
        CharSequence name = nodeDescriptor.getStringAttribute(Constants.StringKey.NAME);
        if (name != null && name.toString().toLowerCase().contains(language)) return name.toString();

        name = checkCapture(nodeDescriptor.getCaptures(Constants.CaptureKey.CAPTURES), language);
        if (name != null) return name.toString();

        name = checkCapture(nodeDescriptor.getCaptures(Constants.CaptureKey.BEGIN_CAPTURES), language);
        if (name != null) return name.toString();

        return checkCapture(nodeDescriptor.getCaptures(Constants.CaptureKey.END_CAPTURES), language);
    }

    private String checkCapture(Int2ObjectMap<CharSequence> captures, final String language) {
        if (captures != null) {
            for (var capture : captures.int2ObjectEntrySet()) {
                String stringCapture = capture.toString().toLowerCase();
                if (stringCapture.contains(language)) return stringCapture;
            }
        }
        return null;
    }

    private @NotNull List<String> extractWordsFromRegexList(@NotNull String language, @NotNull Set<String> keywords, final @NotNull SelectingRegistersStrategy selectingRegisters) {
        return WordExtractionFactory.create(language, keywords, selectingRegisters).extract();
    }
}
