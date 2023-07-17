package com.razerford.ijTextmate.PersistentStorage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "MyFirstStorage", storages = @Storage("FirstStorage.xml"))
public class PersistentStorage implements PersistentStateComponent<PersistentStorage.Test> {
    private Test myTest = new Test();

    @Override
    public @Nullable Test getState() {
        return myTest;
    }

    @Override
    public void loadState(@NotNull Test state) {
        myTest = state;
    }

    public static class Test {
        public String value;

        public String key;

        @Transient
        public String nonSave = "nonSave";
        @Attribute
        private String save = "save";

        public Test() {
        }
    }
}
