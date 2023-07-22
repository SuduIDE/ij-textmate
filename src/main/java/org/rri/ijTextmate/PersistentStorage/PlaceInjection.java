package org.rri.ijTextmate.PersistentStorage;

import java.util.Objects;

public class PlaceInjection {
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
}
