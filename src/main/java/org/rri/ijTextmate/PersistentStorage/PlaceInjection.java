package org.rri.ijTextmate.PersistentStorage;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Objects;

public class PlaceInjection implements LanguageID {
    public static String LANGUAGE_ID = "languageId";
    public static String OFFSET = "offset";
    public String languageId;
    public int offset;

    public PlaceInjection() {
        languageId = "";
    }

    public PlaceInjection(String languageId, final int offset) {
        this.offset = offset;
        this.languageId = languageId;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlaceInjection place && languageId.equals(place.languageId) && offset == place.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageId, offset);
    }

    @Override
    public String getID() {
        return languageId;
    }

    public static class PlaceInjectionAdapter implements JsonSerializer<PlaceInjection>, JsonDeserializer<PlaceInjection> {

        @Override
        public JsonElement serialize(@NotNull PlaceInjection placeInjection, Type type, JsonSerializationContext jsonSerializationContext) {
            if (Objects.equals(placeInjection.languageId, "")) return null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(LANGUAGE_ID, placeInjection.languageId);
            jsonObject.addProperty(OFFSET, placeInjection.offset);
            return jsonObject;
        }

        @Override
        public PlaceInjection deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) return null;
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (!jsonObject.has(LANGUAGE_ID) || !jsonObject.has(OFFSET)) return null;
            String languageID = jsonObject.get(LANGUAGE_ID).getAsString();
            int offset = jsonObject.get(OFFSET).getAsInt();
            return new PlaceInjection(languageID, offset);
        }
    }
}
