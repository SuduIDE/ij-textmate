package org.rri.ijTextmate;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.List;
import java.util.Map;

public class LanguageHighlight implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!(context instanceof PsiLanguageInjectionHost host) || !host.isValidHost()) return;

        TemporaryPlaceInjection languageID = getTemporaryPlaceInjection(host);
        if (languageID == null) return;

        PsiElement psiElement = languageID.hostPointer.getElement();
        if (psiElement == null) return;

        if (!psiElement.getTextRange().intersects(host.getTextRange())) {
            psiElement.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, languageID);
            return;
        }

        String text = host.getText();
        int count = numberCharactersTrim(text);

        if (count == -1) return;

        TextRange range = new TextRange(count, host.getTextLength() - count);
        languageID.register(registrar, host, range);
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLanguageInjectionHost.class);
    }

    private @Nullable TemporaryPlaceInjection getTemporaryPlaceInjection(@NotNull PsiLanguageInjectionHost host) {
        TemporaryPlaceInjection languageID = host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

        if (languageID != null) return languageID;

        Project project = host.getProject();
        PsiFile psiFile = host.getContainingFile();

        Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection> map = TemporaryStorage.getInstance(project)
                .get(InjectorHelper.getRelativePath(project, psiFile)).getMap();

        PsiElement element;
        for (var entry : map.entrySet()) {
            element = entry.getKey().getElement();
            if (element != null && element.getTextRange().intersects(host.getTextRange())) {
                return entry.getValue();
            }
        }

        psiFile = PsiManager.getInstance(project).findFile(psiFile.getOriginalFile().getVirtualFile());
        if (psiFile == null) return null;

        host = PsiTreeUtil.getParentOfType(psiFile.findElementAt(host.getTextOffset()), PsiLanguageInjectionHost.class);
        if (host != null) return host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

        return null;
    }

    @Contract(pure = true)
    private int numberCharactersTrim(@NotNull String text) {
        String escapeCharacters = """
                `'"><
                """;

        int start = 0;
        int end = text.length() - 1;

        if (end == -1) return -1;

        if (text.charAt(start) != text.charAt(end)) return 0;
        int index = escapeCharacters.indexOf(text.charAt(start));

        if (index == -1) return 0;

        char escape = escapeCharacters.charAt(index);

        while (text.charAt(start) == escape && text.charAt(end) == escape && start < end) {
            start++;
            end--;
        }

        return Math.min(start, text.length() - end);
    }
}