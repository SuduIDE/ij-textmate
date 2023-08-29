package org.rri.ijTextmate.Helpers.LanguageInformationExtractor.WordExtraction;

import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordExtractionPug implements WordExtraction {
    private static final Pattern REMOVED = Pattern.compile(regexToRemove(), Pattern.CASE_INSENSITIVE);
    private static final Pattern FILTER = Pattern.compile("([^a-z]*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXTRACT = Pattern.compile(regexToExtract(), Pattern.CASE_INSENSITIVE);
    private final Set<String> keywords;
    private final SelectingRegistersStrategy selectingRegisters;

    public WordExtractionPug(@NotNull Set<String> keywords, final @NotNull SelectingRegistersStrategy selectingRegisters) {
        this.keywords = keywords;
        this.selectingRegisters = selectingRegisters;
    }

    @Override
    public List<String> extract() {
        Set<String> words = new HashSet<>();

        for (String regex : keywords) {
            regex = REMOVED.matcher(regex).replaceAll("");

            if (FILTER.matcher(regex).matches()) continue;

            Matcher extract = EXTRACT.matcher(regex);
            WordExtraction.generateStrings(regex, extract, words, selectingRegisters);
        }

        return new ArrayList<>() {
            {
                addAll(words);
                addAll(words.stream().map(word -> word.replaceFirst("(</?)", "")).toList());
            }
        };
    }

    private static String regexToRemove() {
        return String.format("(%s)", "\\[a-z]?[+?*]?");
    }

    private static String regexToExtract() {
        return String.format("(%s|%s)", "\\([a-z-?()]*\\)", "\\([a-z| ()?]*\\)");
    }
}
