package org.rri.ijTextmate;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Segment;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import org.jetbrains.annotations.Nullable;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.InjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.LeafMultipleInjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.*;

public class LanguageHighlight implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!(context instanceof PsiLanguageInjectionHost host) || !host.isValidHost()) return;

        Project project = host.getProject();
        PsiFile psiFile = host.getContainingFile().getOriginalFile();
        PsiLanguageInjectionHost originalHost = PsiTreeUtil.getParentOfType(psiFile.findElementAt(host.getTextOffset()), PsiLanguageInjectionHost.class);
        if (originalHost == null) return;

        TemporaryPlaceInjection placeInjection = getTemporaryPlaceInjection(originalHost, psiFile, project);
        if (placeInjection != null) {
            PsiElement psiElement = placeInjection.hostPointer.getElement();

            if (psiElement == null) return;

            if (!originalHost.getTextRange().intersects(psiElement.getTextRange())) {
                psiElement.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, placeInjection);
                placeInjection = getTemporaryInjectionPlaceIntersectsHost(host, psiFile, project);
                host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, placeInjection);
                if (placeInjection == null) return;
            }

            List<TextRange> ranges = calculateRanges(host);
            placeInjection.register(registrar, host, ranges);
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
                    return Result.create(rootHost.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE), PsiModificationTracker.getInstance(project));
                }
            });

            if (temporaryPlaceInjection == null || !temporaryPlaceInjection.getStrategyIdentifier().equals("RootMultipleInjectionStrategy")) {
                return;
            }
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

    private @Nullable TemporaryPlaceInjection getTemporaryPlaceInjection(@NotNull PsiLanguageInjectionHost host, PsiFile psiFile, Project project) {
        TemporaryPlaceInjection placeInjection = host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);

        if (placeInjection != null) return placeInjection;

        TemporaryMapPointerToPlaceInjection storage = TemporaryStorage.getInstance(project).get(InjectorHelper.getRelativePath(project, psiFile));
        Set<Map.Entry<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection>> entries = storage.entrySet();
        List<Pair<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection>> newEntrances = new ArrayList<>();

        PsiElement element;
        for (var entry : entries) {
            var key = entry.getKey();
            var value = entry.getValue();
            element = key.getElement();

            if (element != null) {
                if (element.getTextRange().intersects(host.getTextRange())) return value;
                continue;
            }

            Segment segment = key.getRange();
            if (segment == null) continue;
            if (host.getTextRange().intersects(segment)) {
                newEntrances.add(Pair.create(key, value));
            }
        }

        String language = null;
        InjectionStrategy strategy = null;
        for (var pair : newEntrances) {
            language = pair.second.getID();
            strategy = pair.second.getInjectionStrategy();
            storage.remove(pair.first);
        }

        if (language != null && strategy != null) {
            TemporaryPlaceInjection temporaryPlaceInjection = new TemporaryPlaceInjection(SmartPointerManager.createPointer(host), language, strategy);
            storage.put(temporaryPlaceInjection);
            host.putUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE, temporaryPlaceInjection);
            return temporaryPlaceInjection;
        }

        return null;
    }

    private @Nullable TemporaryPlaceInjection getTemporaryInjectionPlaceIntersectsHost(@NotNull PsiLanguageInjectionHost host, PsiFile psiFile, Project project) {
        TemporaryMapPointerToPlaceInjection storage = TemporaryStorage.getInstance(project).get(InjectorHelper.getRelativePath(project, psiFile));
        Set<Map.Entry<SmartPsiElementPointer<PsiLanguageInjectionHost>, TemporaryPlaceInjection>> entries = storage.entrySet();

        for (var entry : entries) {
            var key = entry.getKey();
            var value = entry.getValue();

            Segment segment = key.getRange();
            if (segment != null && host.getTextRange().intersects(segment)) return value;
        }
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