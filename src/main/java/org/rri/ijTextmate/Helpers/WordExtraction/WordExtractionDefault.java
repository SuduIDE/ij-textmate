package org.rri.ijTextmate.Helpers.WordExtraction;

import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordExtractionDefault implements WordExtraction {
    private static final String COMMENT = "\\(\\?\\#.*\\)|\\#\\ \\(.*\\)|\\#.*\\n";
    private static final String SPECIAL_SYMBOLS = "\\\\[a-z]";
    private static final String QUESTION = "\\(\\?[a-z]\\)|\\?[a-z]:?";
    private static final String CONTENT_IN_SQUARE_BRACKETS = "\\[.*\\]";
    private static final String CONTENT_IN_CURLY_BRACKETS = "\\{.*\\}";
    private static final Pattern EXTRACT = Pattern.compile("(\\(@\\)|[a-z|-]*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern REMOVED = Pattern.compile(regexToRemove(), Pattern.CASE_INSENSITIVE);
    private final Set<String> keywords;
    private final SelectingRegistersStrategy selectingRegisters;

    public WordExtractionDefault(@NotNull Set<String> keywords, final @NotNull SelectingRegistersStrategy selectingRegisters) {
        this.keywords = keywords;
        this.selectingRegisters = selectingRegisters;
    }

    @Override
    public List<String> extract() {
        Set<String> words = new HashSet<>();

        for (String regex : keywords) {
            regex = REMOVED.matcher(regex).replaceAll("");

            Matcher extract = EXTRACT.matcher(regex);
            WordExtraction.generateStrings(regex, extract, words, selectingRegisters);
        }
        return words.stream().sorted().toList();
    }

    private static String regexToRemove() {
        return String.format("(%s|%s|%s|%s|%s)", CONTENT_IN_SQUARE_BRACKETS, CONTENT_IN_CURLY_BRACKETS, SPECIAL_SYMBOLS, QUESTION, COMMENT);
    }
}
