package com.razerford.ijTextmate.PersistentStorage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(name = "TemporaryPlace", storages = {@Storage("TemporaryPlace.xml")})
public class TemporaryPlace implements PersistentStateComponent<TemporaryPlace> {
    public String languageId = "state";
    public int offset = 151;

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

    @Override
    public @Nullable TemporaryPlace getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TemporaryPlace state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
