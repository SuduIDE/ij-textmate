package com.razerford.ijTextmate.PersistentStorage;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TemporaryPlace {
    public String languageId;
    public int offset;

    public TemporaryPlace() {
        languageId = "";
    }

    public TemporaryPlace(String languageId, final int offset) {
        this.offset = offset;
        this.languageId = languageId;
    }

    public boolean equals(@NotNull TemporaryPlace place) {
        return languageId.equals(place.languageId) && offset == place.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), languageId, offset);
    }
}
