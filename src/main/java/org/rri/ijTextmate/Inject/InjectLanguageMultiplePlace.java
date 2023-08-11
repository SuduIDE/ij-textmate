package org.rri.ijTextmate.Inject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.MultipleInjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.*;

public class InjectLanguageMultiplePlace extends AbstractInjectLanguage {
    public static InjectLanguageMultiplePlace INSTANCE = new InjectLanguageMultiplePlace();

    public String getIdentifier() {
        return "Inject where used";
    }

    @Override
    protected void addInjectionPlace(@NotNull PsiLanguageInjectionHost host, @NotNull String languageID, PsiFile psiFile, Project project) {
        PsiElement psiElement = host.getParent();
        psiElement = PsiTreeUtil.getChildOfType(psiElement, PsiNamedElement.class);

        if (psiElement == null) {
            InjectLanguageOnePlace.INSTANCE.addInjectionPlace(host, languageID, psiFile, project);
            return;
        }

        Collection<PsiReference> references = ReferencesSearch.search(psiElement).findAll();
        Collection<PsiLanguageInjectionHost> hosts = new ArrayList<>();

        for (PsiReference reference : references) {
            PsiLanguageInjectionHost hostAdd = PsiTreeUtil.getChildOfType(reference.getElement().getParent(), PsiLanguageInjectionHost.class);
            if (hostAdd != null) hosts.add(hostAdd);
        }

        for (PsiLanguageInjectionHost hostAdd : hosts) {
            if (!hostAdd.isValidHost()) return;
            SmartPsiElementPointer<PsiLanguageInjectionHost> psiElementPointer = SmartPointerManager.createPointer(hostAdd);

            TemporaryPlaceInjection temporaryPlaceInjection = new TemporaryPlaceInjection(psiElementPointer, languageID, new MultipleInjectionStrategy());

            String relativePath = InjectorHelper.getRelativePath(project, psiFile);
            TemporaryMapPointerToLanguage mapPointerToLanguage = TemporaryStorage.getInstance(project).get(relativePath);
            mapPointerToLanguage.add(temporaryPlaceInjection);

            putUserData(hostAdd, psiFile, temporaryPlaceInjection);
        }
    }
}
