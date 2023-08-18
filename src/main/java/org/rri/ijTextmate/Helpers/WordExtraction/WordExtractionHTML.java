package org.rri.ijTextmate.Helpers.WordExtraction;

import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordExtractionHTML implements WordExtraction {
    private final Pattern REMOVED_END = Pattern.compile("(\\([?=s\\\\|/>]*\\))", Pattern.CASE_INSENSITIVE);
    private final Pattern REMOVED_SPECIAL_SYMBOLS = Pattern.compile("(\\\\[a-z])", Pattern.CASE_INSENSITIVE);
    private final Pattern REMOVED_QUESTION = Pattern.compile("(\\(\\?[a-z]+\\))", Pattern.CASE_INSENSITIVE);
    private final Pattern FILTER = Pattern.compile("([^a-z]*)", Pattern.CASE_INSENSITIVE);
    private final Pattern EXTRACT_ONE = Pattern.compile("(\\(<\\)\\([a-z0-9|\\[\\]-]*\\))", Pattern.CASE_INSENSITIVE);
    private final Pattern EXTRACT_TWO = Pattern.compile("(\\(</\\)\\([-\\[\\]|a-z0-9]*\\))", Pattern.CASE_INSENSITIVE);
    private final Set<String> keywords;
    private final SelectingRegistersStrategy selectingRegisters;

    public WordExtractionHTML(@NotNull Set<String> keywords, final @NotNull SelectingRegistersStrategy selectingRegisters) {
        this.keywords = keywords;
        this.selectingRegisters = selectingRegisters;
    }

    @Override
    public List<String> extract() {
        Set<String> words = new HashSet<>();

        for (String regex : keywords) {
            regex = REMOVED_END.matcher(regex).replaceAll("");
            regex = REMOVED_SPECIAL_SYMBOLS.matcher(regex).replaceAll("");
            regex = REMOVED_QUESTION.matcher(regex).replaceAll("");

            if (FILTER.matcher(regex).matches()) continue;

            Matcher extract = EXTRACT_ONE.matcher(regex);
            WordExtraction.generateStrings(regex, extract, words, selectingRegisters);
            extract = EXTRACT_TWO.matcher(regex);
            WordExtraction.generateStrings(regex, extract, words, selectingRegisters);
        }
        return words.stream().toList();
    }
}
