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
    public static String LANGUAGE_ID = "languageId";
    public static String START = "start";
    public static String END = "end";
    public String languageId;
    public TextRange textRange = TextRange.EMPTY_RANGE;

    public PlaceInjection() {
        languageId = "";
    }

    public PlaceInjection(String languageId, @NotNull TextRange textRange) {
        this.languageId = languageId;
        this.textRange = textRange;
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
        languageId = placeElement.getAttribute(LANGUAGE_ID).getValue();

        int start = Integer.parseInt(placeElement.getAttribute(START).getValue());
        int end = Integer.parseInt(placeElement.getAttribute(END).getValue());

        if (start > end) {
            return false;
        }
        textRange = new TextRange(start, end);
        return true;
    }

    @Override
    public Element toElement() {
        Element placeJDOM = new Element(PLACE_INJECTION);

        placeJDOM.setAttribute(LANGUAGE_ID, languageId);
        placeJDOM.setAttribute(START, String.valueOf(textRange.getStartOffset()));
        placeJDOM.setAttribute(END, String.valueOf(textRange.getEndOffset()));

        return placeJDOM;
    }

    public static class PlaceInjectionAdapter implements JsonSerializer<PlaceInjection>, JsonDeserializer<PlaceInjection> {

        @Override
        public JsonElement serialize(@NotNull PlaceInjection placeInjection, Type type, JsonSerializationContext jsonSerializationContext) {
            if (Objects.equals(placeInjection.languageId, "")) return null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(LANGUAGE_ID, placeInjection.languageId);
            jsonObject.addProperty(START, placeInjection.textRange.getStartOffset());
            jsonObject.addProperty(END, placeInjection.textRange.getEndOffset());
            return jsonObject;
        }

        @Override
        public PlaceInjection deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) return null;
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (!jsonObject.has(LANGUAGE_ID) || !jsonObject.has(START) || !jsonObject.has(END)) return null;

            String languageID = jsonObject.get(LANGUAGE_ID).getAsString();
            TextRange textRange = createTextRangeFromJsonObject(jsonObject);

            return new PlaceInjection(languageID, textRange);
        }

        private @NotNull TextRange createTextRangeFromJsonObject(@NotNull JsonObject jsonObject) {
            int start = jsonObject.get(START).getAsInt();
            int end = jsonObject.get(END).getAsInt();
            return new TextRange(start, end);
        }
    }
}
