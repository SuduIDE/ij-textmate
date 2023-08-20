package org.rri.ijTextmate.Helpers.WordExtraction;

import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordExtractionDefault implements WordExtraction {
    private static final Pattern EXTRACT = Pattern.compile("(\\(@\\)|[a-z|-]*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern REMOVED_COMMENT = Pattern.compile("(\\(\\?\\#.*\\)|\\#\\ \\(.*\\)|\\#.*\\n)", Pattern.CASE_INSENSITIVE);
    private static final Pattern REMOVED_SPECIAL_SYMBOLS = Pattern.compile("(\\\\[a-z])", Pattern.CASE_INSENSITIVE);
    private static final Pattern REMOVED_QUESTION = Pattern.compile("(~\\?|\\(\\?[a-z]\\)|\\?[a-z]:?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern REMOVED_IN_SQUARE_BRACKETS = Pattern.compile("(\\[.*\\])", Pattern.CASE_INSENSITIVE);
    private static final Pattern REMOVED_IN_CURLY_BRACKETS = Pattern.compile("(\\{.*\\})", Pattern.CASE_INSENSITIVE);
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
            regex = REMOVED_IN_SQUARE_BRACKETS.matcher(regex).replaceAll("");
            regex = REMOVED_IN_CURLY_BRACKETS.matcher(regex).replaceAll("");
            regex = REMOVED_SPECIAL_SYMBOLS.matcher(regex).replaceAll("");
            regex = REMOVED_QUESTION.matcher(regex).replaceAll("");
            regex = REMOVED_COMMENT.matcher(regex).replaceAll("");

            Matcher extract = EXTRACT.matcher(regex);
            WordExtraction.generateStrings(regex, extract, words, selectingRegisters);
        }
        return words.stream().sorted().toList();
    }
}
