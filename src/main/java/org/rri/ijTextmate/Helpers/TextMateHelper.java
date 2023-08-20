package org.rri.ijTextmate.Helpers;

import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.custom.SyntaxTable;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.openapi.project.Project;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.Constants;
import org.jetbrains.plugins.textmate.TextMateService;
import org.jetbrains.plugins.textmate.bundles.*;
import org.jetbrains.plugins.textmate.configuration.*;
import org.jetbrains.plugins.textmate.language.TextMateLanguageDescriptor;
import org.jetbrains.plugins.textmate.language.syntax.SyntaxNodeDescriptor;
import org.jetbrains.plugins.textmate.plist.*;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;
import org.rri.ijTextmate.Helpers.WordExtraction.WordExtractionFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Service(Service.Level.PROJECT)
public final class TextMateHelper {
    private final static Icon DEFAULT_ICON = AllIcons.Actions.Words;
    private final Map<String, String> languages = new HashMap<>();
    private final Map<String, String> languageToFileExtension = new HashMap<>(Map.of("textmate", ""));
    private final Map<String, List<String>> languageToFileExtensions = new HashMap<>();
    private final Map<String, List<String>> languageToKeywords = new HashMap<>(Map.of("textmate", Collections.emptyList()));
    private final Map<String, SelectingRegistersStrategy> languageToStrategyRegister = new HashMap<>(Map.of("docker", SelectingRegistersStrategy.UPPER, "sql", SelectingRegistersStrategy.UPPER));
    private final PlistReader plistReader = new CompositePlistReader();
    private final BundleFactory bundleFactory = new BundleFactory(plistReader);
    private final Map<String, AbstractFileType> extensionToFileType = new HashMap<>();
    private final Map<String, Icon> languageToIcon = new HashMap<>();

    public TextMateHelper() {
        updateLanguages();

        for (FileType fileType : FileTypeManager.getInstance().getRegisteredFileTypes()) {
            if (fileType instanceof AbstractFileType abstractFileType) {
                String extension = abstractFileType.getDefaultExtension();
                extensionToFileType.put(extension.isEmpty() ? abstractFileType.getName().toLowerCase() : extension, abstractFileType);
            }
        }
    }

    public void updateLanguages() {
        languages.clear();
        languages.put("textmate", "");

        Collection<BundleConfigBean> bundles = TextMateSettings.getInstance().getBundles();
        bundles.forEach((bundle) -> languages.put(bundle.getName(), bundle.getPath()));
    }

    public List<String> getLanguages() {
        return languages.keySet().stream().toList();
    }

    public @NotNull Path getPath(String language) {
        return Path.of(languages.get(language));
    }

    public @NotNull String getExtension(String language) {
        String fileExtension = languageToFileExtension.get(language);
        if (fileExtension != null) return fileExtension;

        synchronized (this) {
            fileExtension = languageToFileExtension.get(language);
            if (fileExtension == null) {
                List<String> extensions = getExtensions(getPath(language));
                languageToFileExtensions.put(language, extensions);

                fileExtension = calcExtension(language);
                languageToFileExtension.put(language, fileExtension);

                String extension = abstractFileTypeExists(extensions);

                ReadAction.run(() -> {
                    List<String> keywords;
                    Icon icon = null;
                    if (extension == null) {
                        keywords = extractKeywordsFromTextmateRegex(language);
                    } else {
                        AbstractFileType abstractFileType = extensionToFileType.get(extension);
                        keywords = extractKeywordsFromAbstractLanguage(abstractFileType);
                        icon = abstractFileType.getIcon();
                    }
                    if (keywords.isEmpty()) keywords = extractKeywordsFromTextmateRegex(language);
                    languageToKeywords.put(language, keywords);
                    languageToIcon.put(language, (icon == null) ? DEFAULT_ICON : icon);
                });
            }
        }

        return fileExtension;
    }

    public @NotNull List<String> getKeywords(String language) {
        return languageToKeywords.get(language);
    }

    public @NotNull Icon getIcon(String language) {
        return languageToIcon.get(language);
    }

    private @Nullable Bundle createBundle(Path path) {
        try {
            return bundleFactory.fromDirectory(path.toFile());
        } catch (Throwable ignored) {
        }
        return null;
    }

    private @NotNull List<String> getExtensions(@NotNull Path path) {
        Bundle bundle = createBundle(path);
        if (bundle == null) return Collections.emptyList();

        List<String> allExtension = new ArrayList<>();

        for (File grammarFile : bundle.getGrammarFiles()) {
            try {
                Plist plist = plistReader.read(grammarFile);
                allExtension.addAll(bundle.getExtensions(grammarFile, plist));
            } catch (IOException ignored) {
            }
        }

        return allExtension;
    }

    private @NotNull String calcExtension(String language) {
        List<String> extensions = languageToFileExtensions.get(language);
        if (extensions.contains(language.toLowerCase())) return language.toLowerCase();
        if (extensions.isEmpty()) return "";
        return extensions.get(0);
    }

    private @Nullable String abstractFileTypeExists(List<String> extensions) {
        for (String extension : extensionToFileType.keySet()) {
            if (extensions.contains(extension)) return extension;
        }
        return null;
    }

    private @NotNull List<String> extractKeywordsFromAbstractLanguage(@NotNull AbstractFileType abstractFileType) {
        SyntaxTable syntaxTable = abstractFileType.getSyntaxTable();
        Set<String> merged = new HashSet<>() {
            {
                addAll(syntaxTable.getKeywords1());
                addAll(syntaxTable.getKeywords2());
                addAll(syntaxTable.getKeywords3());
                addAll(syntaxTable.getKeywords4());
            }
        };
        return merged.stream().toList();
    }

    private @NotNull List<String> extractKeywordsFromTextmateRegex(String language) {
        Set<String> keywords = new HashSet<>();
        Set<String> visited = new HashSet<>();

        for (String extension : languageToFileExtensions.get(language)) {
            TextMateLanguageDescriptor textMateLanguageDescriptor = TextMateService.getInstance().getLanguageDescriptorByExtension(extension);
            if (textMateLanguageDescriptor == null) continue;

            SyntaxNodeDescriptor nodeDescriptor = textMateLanguageDescriptor.getRootSyntaxNode();
            visited.add(nodeDescriptor.toString());
            recursiveExtractionRegex(nodeDescriptor, keywords, visited, language);
        }
        return extractWordsFromRegexList(language, keywords, getStrategy(language));
    }

    private SelectingRegistersStrategy getStrategy(String language) {
        SelectingRegistersStrategy strategy = languageToStrategyRegister.get(language);
        if (strategy != null) return strategy;
        return SelectingRegistersStrategy.DEFAULT;
    }

    private void recursiveExtractionRegex(@NotNull SyntaxNodeDescriptor syntaxNodeDescriptor, @NotNull Set<String> keywords, Set<String> visited, final String language) {
        for (SyntaxNodeDescriptor nodeDescriptor : syntaxNodeDescriptor.getChildren()) {
            if (visited.contains(nodeDescriptor.toString())) continue;
            visited.add(nodeDescriptor.toString());

            recursiveExtractionRegex(nodeDescriptor, keywords, visited, language);

            extractFromNode(nodeDescriptor, keywords, language.toLowerCase());
        }
    }

    private void extractFromNode(@NotNull SyntaxNodeDescriptor nodeDescriptor, @NotNull Set<String> keywords, final String language) {
        String name = tryAdd(nodeDescriptor, language);
        if (name == null) return;

        CharSequence variable = nodeDescriptor.getStringAttribute(Constants.StringKey.MATCH);
        if (variable != null) keywords.add(variable.toString());

        variable = nodeDescriptor.getStringAttribute(Constants.StringKey.BEGIN);
        if (variable != null) keywords.add(variable.toString());

        variable = nodeDescriptor.getStringAttribute(Constants.StringKey.END);
        if (variable != null) keywords.add(variable.toString());
    }

    private String tryAdd(@NotNull SyntaxNodeDescriptor nodeDescriptor, final String language) {
        CharSequence name = nodeDescriptor.getStringAttribute(Constants.StringKey.NAME);
        if (name != null && name.toString().toLowerCase().contains(language)) return name.toString();

        name = checkCapture(nodeDescriptor.getCaptures(Constants.CaptureKey.CAPTURES), language);
        if (name != null) return name.toString();

        name = checkCapture(nodeDescriptor.getCaptures(Constants.CaptureKey.BEGIN_CAPTURES), language);
        if (name != null) return name.toString();

        return checkCapture(nodeDescriptor.getCaptures(Constants.CaptureKey.END_CAPTURES), language);
    }

    private String checkCapture(Int2ObjectMap<CharSequence> captures, final String language) {
        if (captures != null) {
            for (var capture : captures.int2ObjectEntrySet()) {
                String stringCapture = capture.toString().toLowerCase();
                if (stringCapture.contains(language)) return stringCapture;
            }
        }
        return null;
    }

    private @NotNull List<String> extractWordsFromRegexList(@NotNull String language, @NotNull Set<String> keywords, final @NotNull SelectingRegistersStrategy selectingRegisters) {
        return WordExtractionFactory.create(language, keywords, selectingRegisters).extract();
    }

    public static @NotNull TextMateHelper getInstance(@NotNull Project project) {
        return project.getService(TextMateHelper.class);
    }

    public static @NotNull TextMateHelper upateLanguagesAndGetTextMateHelper(@NotNull Project project) {
        TextMateHelper textMateHelper = getInstance(project);
        textMateHelper.updateLanguages();
        return textMateHelper;
    }
}
