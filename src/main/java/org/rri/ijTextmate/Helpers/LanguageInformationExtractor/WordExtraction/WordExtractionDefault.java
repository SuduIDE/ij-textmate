package org.rri.ijTextmate.Helpers.LanguageInformationExtractor.WordExtraction;

import com.intellij.openapi.util.TextRange;
import org.jcodings.specific.UTF8Encoding;
import org.jetbrains.annotations.NotNull;
import org.joni.Option;
import org.joni.Regex;
import org.joni.Region;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordExtractionDefault implements WordExtraction {
    private static final Comparator<TextRange> RANGE_COMPARATOR = (rangeLeft, rangeRight) -> {
        if (rangeLeft.intersects(rangeRight)) return 0;
        return rangeLeft.getStartOffset() - rangeRight.getStartOffset();
    };
    private static final String COMMENT = "\\(\\?\\#.*\\)|\\#\\ \\(.*\\)|\\#.*\\n";
    private static final String SPECIAL_SYMBOLS = "\\\\[a-z]";
    private static final String CONTENT_IN_SQUARE_BRACKETS = "\\[.*\\]\\??";
    private static final String CONTENT_IN_CURLY_BRACKETS = "\\{.*\\}";
    private static final String RECURSIVE_CAPTURE = "(?=\\(((?:[a-zA-Z0-9@?:|_-]++|\\(\\)|\\(\\g<1>\\))++)\\))";
    private static final Pattern QUESTION = Pattern.compile("\\(\\?[a-z-]+:?");
    private static final Pattern EXTRACT = Pattern.compile("(\\?:|\\(@\\)[a-z0-9_|-]*|[a-z0-9_|-]*)", Pattern.CASE_INSENSITIVE);
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
            regex = removeQuestion(regex);

            Matcher extract = EXTRACT.matcher(regex);
            Set<TextRange> exceptions = recursiveCapture(regex);
            generateStrings(regex, extract, words, exceptions, selectingRegisters);
            generateStrings(regex, words, exceptions, selectingRegisters);
        }
        return words.stream().sorted().toList();
    }

    private static String regexToRemove() {
        return String.format("(%s|%s|%s|%s)", CONTENT_IN_SQUARE_BRACKETS, CONTENT_IN_CURLY_BRACKETS, COMMENT, SPECIAL_SYMBOLS);
    }

    private void generateStrings(String regex, @NotNull Matcher extract, Set<String> words, Set<TextRange> exceptions, SelectingRegistersStrategy selectingRegisters) {
        while (extract.find()) {
            int start = extract.start();
            int end = extract.end();
            if (exceptions.contains(new TextRange(start, end))) continue;

            String added = regex.substring(start, end);

            WordExtraction.generateStrings(added, words, selectingRegisters);
        }
    }

    private void generateStrings(String regex, Set<String> words, @NotNull Set<TextRange> ranges, SelectingRegistersStrategy selectingRegisters) {
        for (TextRange range : ranges) {

            String added = String.format("(%s)", regex.substring(range.getStartOffset(), range.getEndOffset()));

            WordExtraction.generateStrings(added, words, selectingRegisters);
        }
    }

    private @NotNull String removeQuestion(String regex) {
        Matcher matcher = QUESTION.matcher(regex);

        List<TextRange> ranges = new ArrayList<>();
        while (matcher.find()) ranges.add(new TextRange(matcher.start() + 1, matcher.end()));
        for (int i = ranges.size() - 1; i > -1; i--) {
            TextRange range = ranges.get(i);
            regex = regex.substring(0, range.getStartOffset()) + regex.substring(range.getEndOffset());
        }
        return regex;
    }

    private @NotNull Set<TextRange> recursiveCapture(@NotNull String str) {
        byte[] captureBytes = RECURSIVE_CAPTURE.getBytes(StandardCharsets.UTF_8);
        Regex regex = new Regex(captureBytes, 0, captureBytes.length, Option.CAPTURE_GROUP, UTF8Encoding.INSTANCE);

        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);

        org.joni.Matcher matcher = regex.matcher(strBytes);

        int searched = matcher.search(0, strBytes.length, Option.DEFAULT);

        Set<TextRange> result = new TreeSet<>(RANGE_COMPARATOR);

        while (searched > -1) {
            Region matchedRegion = matcher.getEagerRegion();
            if (matchedRegion != null) {
                for (int i = 0; i < matchedRegion.numRegs; i++) {
                    int begin = Math.max(matchedRegion.beg[i], 0);
                    int end = Math.max(matchedRegion.end[i], 0);
                    if (begin < end) result.add(new TextRange(begin, end));
                    searched = end;
                }
            }
            searched = matcher.search(searched, strBytes.length, Option.DEFAULT);
        }
        return result;
    }
}
