package org.rri.ijTextmate.Helpers.LanguageInformationExtractor.WordExtraction;

import com.github.curiousoddman.rgxgen.RgxGen;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface WordExtraction {
    List<String> extract();
    int LIMIT_LENGTH = 2;
    Pattern FILTER = Pattern.compile("([^a-z]*)", Pattern.CASE_INSENSITIVE);

    static void generateStrings(String regex, @NotNull Matcher extract, Set<String> words, SelectingRegistersStrategy selectingRegisters) {
        while (extract.find()) {
            String added = regex.substring(extract.start(), extract.end());

            if (added.isEmpty()) continue;
            if (FILTER.matcher(added).matches()) continue;
            var rgxgen = new RgxGen(added);
            if (rgxgen.getUniqueEstimation().isEmpty()) continue;
            var it = rgxgen.iterateUnique();

            while (it.hasNext()) {
                String word = it.next();
                if (word.length() < LIMIT_LENGTH) continue;
                words.add(selectingRegisters.apply(word));
            }
        }
    }
}
