package com.razerford.ijTextmate.Inject;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.razerford.ijTextmate.PersistentStorage.PersistentStorage;
import com.razerford.ijTextmate.PersistentStorage.MyTemporaryPlacesRegistry;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InjectLanguage {
    public static final Key<InjectedLanguage> MY_TEMPORARY_INJECTED_LANGUAGE = Key.create("MY_TEMPORARY_INJECTED_LANGUAGE");

    public static void inject(@NotNull PsiLanguageInjectionHost host, InjectedLanguage language, Project project) {
        InjectedLanguage prevLanguage = host.getUserData(MY_TEMPORARY_INJECTED_LANGUAGE);
        SmartPsiElementPointer<PsiLanguageInjectionHost> pointer = SmartPointerManager.getInstance(project).createSmartPsiElementPointer(host);
        MyTemporaryPlacesRegistry.TemporaryPlace prevPlace = new MyTemporaryPlacesRegistry.TemporaryPlace(prevLanguage, pointer);
        MyTemporaryPlacesRegistry.TemporaryPlace place = new MyTemporaryPlacesRegistry.TemporaryPlace(language, pointer);
        PersistentStorage.SetElement elements = project.getService(PersistentStorage.class).getState();
        elements.addElement(pointer);
        WriteCommandAction.runWriteCommandAction(project, () -> addInjectionPlace(place, project));
    }

    private static void addInjectionPlace(MyTemporaryPlacesRegistry.@NotNull TemporaryPlace place, Project project) {
        PsiLanguageInjectionHost host = place.psiElementPointer.getElement();
        if (host == null) return;
        PsiElement element = host.getOriginalElement();
        if (element != null) element = element.getParent();
        PsiReference psiReference = getFirstReference(element);
        if (!(element instanceof PsiNameIdentifierOwner) && psiReference != null) {
            element = psiReference.resolve();
            PsiLanguageInjectionHost newHost = getHostFromElementRoot(element);
            host = (newHost == null) ? host : newHost;
        }
        host.putUserData(MY_TEMPORARY_INJECTED_LANGUAGE, place.language);
        host.getManager().dropPsiCaches();
    }

    @Contract(pure = true)
    public static @Nullable PsiLanguageInjectionHost getHostFromElementRoot(PsiElement root) {
        if (root == null) return null;
        for (PsiElement element : root.getChildren()) {
            if (element instanceof PsiLanguageInjectionHost host) {
                return host;
            }
        }
        return null;
    }

    @Contract(pure = true)
    public static @Nullable PsiReference getFirstReference(PsiElement root) {
        if (root == null) return null;
        for (PsiElement element : root.getChildren()) {
            if (element instanceof PsiReference reference) {
                return reference;
            }
        }
        return null;
    }
}
