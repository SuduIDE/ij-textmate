package org.rri.ijTextmate.Helpers;

import com.intellij.ide.highlighter.custom.SyntaxTable;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.Constants;
import org.jetbrains.plugins.textmate.TextMateService;
import org.jetbrains.plugins.textmate.bundles.*;
import org.jetbrains.plugins.textmate.configuration.*;
import org.jetbrains.plugins.textmate.language.TextMateLanguageDescriptor;
import org.jetbrains.plugins.textmate.language.syntax.SyntaxNodeDescriptor;
import org.jetbrains.plugins.textmate.plist.*;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Path;
import java.util.*;

@Service(Service.Level.PROJECT)
public final class TextMateHelper {
    private static final String SEARCH = "([a-z_]+[a-z_]*)";
    private static final String REMOVED = "(\\\\[a-z])";
    private static final String FILTER = "_*";
    private static final Pattern PATTERN_SEARCH = Pattern.compile(SEARCH, Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_REMOVED = Pattern.compile(REMOVED, Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_FILTER = Pattern.compile(FILTER, Pattern.CASE_INSENSITIVE);
    private final Map<String, String> languages = new HashMap<>();
    private final Map<String, String> languageToFileExtension = new HashMap<>(Map.of("textmate", ""));
    private final Map<String, List<String>> languageToFileExtensions = new HashMap<>();
    private final Map<String, List<String>> languageToKeywords = new HashMap<>(Map.of("textmate", Collections.emptyList()));
    private final Map<String, SelectingRegistersStrategy> languageToStrategyRegister = new HashMap<>(Map.of("docker", SelectingRegistersStrategy.UPPER, "sql", SelectingRegistersStrategy.UPPER));
    private final PlistReader plistReader = new CompositePlistReader();
    private final BundleFactory bundleFactory = new BundleFactory(plistReader);
    private final Map<String, AbstractFileType> extensionToFileType = new HashMap<>();

    public TextMateHelper() {
        updateLanguages();

        for (FileType fileType : FileTypeManager.getInstance().getRegisteredFileTypes()) {
            if (fileType instanceof AbstractFileType abstractFileType) {
                extensionToFileType.put(abstractFileType.getDefaultExtension(), abstractFileType);
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
                    if (extension == null) {
                        keywords = extractKeywordsFromTextmateRegex(language);
                    } else {
                        keywords = extractKeywordsFromAbstractLanguage(extensionToFileType.get(extension));
                    }
                    if (keywords.isEmpty()) keywords = extractKeywordsFromTextmateRegex(language);
                    languageToKeywords.put(language, keywords);
                });
            }
        }

        return fileExtension;
    }

    public @NotNull List<String> getKeywords(String language) {
        return languageToKeywords.get(language);
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
        ArrayList<String> keywords = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String extension : languageToFileExtensions.get(language)) {
            TextMateLanguageDescriptor textMateLanguageDescriptor = TextMateService.getInstance().getLanguageDescriptorByExtension(extension);
            if (textMateLanguageDescriptor == null) continue;

            SyntaxNodeDescriptor nodeDescriptor = textMateLanguageDescriptor.getRootSyntaxNode();
            visited.add(nodeDescriptor.toString());
            recursiveExtractionRegex(nodeDescriptor, keywords, visited);
        }
        return extractWordsFromRegexList(keywords, getStrategy(language));
    }

    private SelectingRegistersStrategy getStrategy(String language) {
        SelectingRegistersStrategy strategy = languageToStrategyRegister.get(language);
        if (strategy != null) return strategy;
        return SelectingRegistersStrategy.DEFAULT;
    }

    private void recursiveExtractionRegex(@NotNull SyntaxNodeDescriptor syntaxNodeDescriptor, @NotNull ArrayList<String> keywords, Set<String> visited) {
        for (SyntaxNodeDescriptor nodeDescriptor : syntaxNodeDescriptor.getChildren()) {
            if (visited.contains(nodeDescriptor.toString())) continue;
            visited.add(nodeDescriptor.toString());

            recursiveExtractionRegex(nodeDescriptor, keywords, visited);

            CharSequence keyword = nodeDescriptor.getStringAttribute(Constants.StringKey.MATCH);
            if (keyword != null) keywords.add(keyword.toString());
        }
    }

    private @NotNull List<String> extractWordsFromRegexList(@NotNull List<String> keywords, final @NotNull SelectingRegistersStrategy selectingRegisters) {
        Set<String> words = new HashSet<>();

        for (String regex : keywords) {
            regex = PATTERN_REMOVED.matcher(regex).replaceAll("");
            Matcher matcher = PATTERN_SEARCH.matcher(regex);

            while (matcher.find()) {
                String added = regex.substring(matcher.start(), matcher.end());
                if (!PATTERN_FILTER.matcher(added).matches() && added.length() > 1) {
                    words.add(selectingRegisters.apply(added));
                }
            }
        }
        return words.stream().toList();
    }

    public static @NotNull TextMateHelper getInstance(@NotNull Project project) {
        return project.getService(TextMateHelper.class);
    }

    public static @NotNull TextMateHelper upateLanguagesAndGetTextMateHelper(@NotNull Project project) {
        TextMateHelper textMateHelper = getInstance(project);
        textMateHelper.updateLanguages();
        return textMateHelper;
    }

    @FunctionalInterface
    private interface SelectingRegistersStrategy {
        String apply(String word);

        SelectingRegistersStrategy DEFAULT = new DefaultStrategySelectingRegisters();
        SelectingRegistersStrategy UPPER = new UpperStrategySelectingRegisters();
        @SuppressWarnings("unused")
        SelectingRegistersStrategy LOWER = new LowerStrategySelectingRegisters();

        class DefaultStrategySelectingRegisters implements SelectingRegistersStrategy {
            @Override
            public String apply(@NotNull String word) {
                return word;
            }
        }


        class UpperStrategySelectingRegisters implements SelectingRegistersStrategy {
            @Override
            public String apply(@NotNull String word) {
                return word.toUpperCase();
            }
        }

        class LowerStrategySelectingRegisters implements SelectingRegistersStrategy {
            @Override
            public String apply(@NotNull String word) {
                return word.toLowerCase();
            }
        }
    }
}
