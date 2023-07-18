package com.razerford.ijTextmate.PersistentStorage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@State(name = "PersistentStorage", storages = @Storage("PersistentStorage.xml"))
public class PersistentStorage implements PersistentStateComponent<PersistentStorage.SetElement> {
    private SetElement mySetElement = new SetElement();

    @Override
    public @NotNull SetElement getState() {
        return mySetElement;
    }

    @Override
    public void loadState(@NotNull SetElement state) {
        mySetElement = state;
    }

    public static class SetElement {
        @Attribute
        private final Set<SmartPsiElementPointer<? extends PsiLanguageInjectionHost>> set = new HashSet<>();

        public SetElement() {
        }

        public boolean addElement(SmartPsiElementPointer<? extends PsiLanguageInjectionHost> elementPointer) {
            return set.add(elementPointer);
        }

        public boolean contains(SmartPsiElementPointer<? extends PsiLanguageInjectionHost> elementPointer) {
            return set.contains(elementPointer);
        }

        public boolean remove(SmartPsiElementPointer<? extends PsiLanguageInjectionHost> elementPointer) {
            return set.remove(elementPointer);
        }

        public Set<SmartPsiElementPointer<? extends PsiLanguageInjectionHost>> getElements() {
            return set;
        }
    }
}
