package org.rri.ijTextmate.Helpers.WordExtraction;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.util.Set;

public class WordExtractionFactory {
    @Contract(pure = true)
    public static @NotNull WordExtraction create(@NotNull String language, @NotNull Set<String> keywords, final @NotNull SelectingRegistersStrategy selectingRegisters) {
        return switch (language) {
            case "html" -> new WordExtractionHTML(keywords, selectingRegisters);
            default -> new WordExtractionDefault(keywords, selectingRegisters);
        };
    }
}
