package org.rri.ijTextmate.Storage.PersistentStorage;

import com.google.gson.*;
import com.intellij.openapi.util.TextRange;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.Interfaces.ConverterElement;
import org.rri.ijTextmate.Storage.Interfaces.LanguageID;

import java.lang.reflect.Type;
import java.util.Objects;

public class PlaceInjection implements LanguageID, ConverterElement {
    private static final String PLACE_INJECTION = "placeInjection";
    private static final String IDENTIFIER_STRATEGY = "identifierStrategy";
    public static String LANGUAGE_ID = "languageId";
    public static String START = "start";
    public static String END = "end";
    public String languageId;
    public TextRange textRange;
    public String identifierStrategy;

    public PlaceInjection() {
        languageId = "";
        textRange = TextRange.EMPTY_RANGE;
        identifierStrategy = "";
    }

    public PlaceInjection(@NotNull String languageId, @NotNull TextRange textRange, @NotNull String identifierStrategy) {
        this.languageId = languageId;
        this.textRange = textRange;
        this.identifierStrategy = identifierStrategy;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlaceInjection place && languageId.equals(place.languageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageId, textRange);
    }

    public int getCenter() {
        return (textRange.getStartOffset() + textRange.getEndOffset()) / 2;
    }

    @Override
    public String getID() {
        return languageId;
    }

    @Override
    public boolean fromElement(@NotNull Element placeElement) {
        int start = Integer.parseInt(placeElement.getAttribute(START).getValue());
        int end = Integer.parseInt(placeElement.getAttribute(END).getValue());

        if (start > end) {
            return false;
        }
        textRange = new TextRange(start, end);

        languageId = placeElement.getAttribute(LANGUAGE_ID).getValue();
        identifierStrategy = placeElement.getAttribute(IDENTIFIER_STRATEGY).getValue();

        return true;
    }

    @Override
    public Element toElement() {
        Element placeJDOM = new Element(PLACE_INJECTION);

        placeJDOM.setAttribute(LANGUAGE_ID, languageId);
        placeJDOM.setAttribute(IDENTIFIER_STRATEGY, identifierStrategy);
        placeJDOM.setAttribute(START, String.valueOf(textRange.getStartOffset()));
        placeJDOM.setAttribute(END, String.valueOf(textRange.getEndOffset()));

        return placeJDOM;
    }
}
