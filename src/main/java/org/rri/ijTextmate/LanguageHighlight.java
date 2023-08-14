package org.rri.ijTextmate;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import org.jetbrains.annotations.Nullable;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.InjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.LeafMultipleInjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.*;

public class LanguageHighlight implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!(context instanceof PsiLanguageInjectionHost host) || !host.isValidHost()) return;

        TemporaryPlaceInjection languageID = getTemporaryPlaceInjection(host);
        if (languageID != null) {

            PsiElement psiElement = languageID.hostPointer.getElement();
            if (psiElement == null) return;

            if (!psiElement.getTextRange().intersects(host.getTextRange())) {
                psiElement.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, languageID);
                return;
            }

            List<TextRange> ranges = calculateRanges(host);
            languageID.register(registrar, host, ranges);
        } else {
            TemporaryPlaceInjection temporaryPlaceInjection = CachedValuesManager.getCachedValue(host, Constants.MY_CACHED_TEMPORARY_INJECTED_LANGUAGE, new CachedValueProvider<>() {
                @Override
                public @Nullable Result<TemporaryPlaceInjection> compute() {
                    PsiElement element = host.getParent();
                    PsiReference reference = null;
                    for (PsiElement child : element.getChildren()) {
                        if (child instanceof PsiReference newReference) {
                            reference = newReference;
                            break;
                        }
                    }
                    if (reference == null) return null;

                    element = reference.resolve();
                    PsiLanguageInjectionHost rootHost = PsiTreeUtil.findChildOfType(element, PsiLanguageInjectionHost.class);
                    if (rootHost == null) return null;
                    return Result.create(rootHost.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE), PsiModificationTracker.getInstance(host.getProject()));
                }
            });

            if (temporaryPlaceInjection == null || !temporaryPlaceInjection.getStrategyIdentifier().equals("RootMultipleInjectionStrategy"))
                return;

            SmartPsiElementPointer<PsiLanguageInjectionHost> pointer = SmartPointerManager.createPointer(host);
            String language = temporaryPlaceInjection.languageID;
            InjectionStrategy injectionStrategy = new LeafMultipleInjectionStrategy(temporaryPlaceInjection);

            TemporaryPlaceInjection newTempPlaceInjection = new TemporaryPlaceInjection(pointer, language, injectionStrategy);

            host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, newTempPlaceInjection);

            List<TextRange> ranges = calculateRanges(host);
            newTempPlaceInjection.register(registrar, host, ranges);
        }
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

    private static @NotNull List<TextRange> calculateRanges(PsiLanguageInjectionHost host) {
        TextRange textRange = ElementManipulators.getValueTextRange(host);
        String text = host.getText();
        int indent = calculateIndent(text.substring(textRange.getStartOffset(), textRange.getEndOffset()));

        if (indent == 0) return Collections.singletonList(textRange);

        int startOffset = textRange.getStartOffset() + indent;
        int endOffset = text.indexOf('\n', startOffset);
        List<TextRange> list = new ArrayList<>();

        while (endOffset > 0) {
            endOffset++;
            list.add(new TextRange(startOffset, endOffset));
            startOffset = endOffset + indent;
            endOffset = text.indexOf('\n', startOffset);
        }

        endOffset = textRange.getEndOffset();

        if (startOffset < endOffset) list.add(new TextRange(startOffset, endOffset));

        return list;
    }

    private static int calculateIndent(@NotNull String text) {
        if (!text.contains("\n")) return 0;

        String[] strings = text.split("\n");
        int indent = Integer.MAX_VALUE;

        for (String str : strings) {
            int curIndent = 0, i = 0;
            while (i < str.length() && str.charAt(i++) == ' ') curIndent++;
            indent = Integer.min(curIndent, indent);
        }
        return indent;
    }
}