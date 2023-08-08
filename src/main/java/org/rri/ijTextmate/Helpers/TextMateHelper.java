package org.rri.ijTextmate.Helpers;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
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
    private final Map<String, String> languages = new HashMap<>();
    private final Map<String, String> languageToFileExtension = new HashMap<>(Map.of("textmate", ""));
    private final Map<String, List<String>> languageToFileExtensions = new HashMap<>();
    private final Map<String, List<String>> languageToKeywords = new HashMap<>(Map.of("textmate", Collections.emptyList()));
    private final Map<String, StrategySelectingRegisters> languageToStrategy = new HashMap<>(Map.of("docker", StrategySelectingRegisters.UPPER, "sql", StrategySelectingRegisters.UPPER));
    private final PlistReader plistReader = new CompositePlistReader();
    private final BundleFactory bundleFactory = new BundleFactory(plistReader);

    public TextMateHelper() {
        updateLanguages();
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

                ReadAction.run(() -> languageToKeywords.put(language, calcKeywords(language)));
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

    private @NotNull List<String> calcKeywords(String language) {
        ArrayList<String> keywords = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String extension : languageToFileExtensions.get(language)) {
            TextMateLanguageDescriptor textMateLanguageDescriptor = TextMateService.getInstance().getLanguageDescriptorByExtension(extension);
            if (textMateLanguageDescriptor == null) continue;

            SyntaxNodeDescriptor nodeDescriptor = textMateLanguageDescriptor.getRootSyntaxNode();
            visited.add(nodeDescriptor.toString());
            recursiveExtraction(nodeDescriptor, keywords, visited);
        }
        return splitRegex(keywords, getStrategy(language));
    }

    private StrategySelectingRegisters getStrategy(String language) {
        StrategySelectingRegisters strategy = languageToStrategy.get(language);
        if (strategy != null) return strategy;
        return StrategySelectingRegisters.DEFAULT;
    }

    private void recursiveExtraction(@NotNull SyntaxNodeDescriptor syntaxNodeDescriptor, @NotNull ArrayList<String> keywords, Set<String> visited) {
        for (SyntaxNodeDescriptor nodeDescriptor : syntaxNodeDescriptor.getChildren()) {
            if (visited.contains(nodeDescriptor.toString())) continue;
            visited.add(nodeDescriptor.toString());

            recursiveExtraction(nodeDescriptor, keywords, visited);

            CharSequence keyword = nodeDescriptor.getStringAttribute(Constants.StringKey.MATCH);
            if (keyword != null) keywords.add(keyword.toString());
        }
    }

    private @NotNull List<String> splitRegex(@NotNull List<String> keywords, final StrategySelectingRegisters selectingRegisters) {
        String REGEX = "([a-z]+[a-z_]*)";
        Pattern pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
        Set<String> set = new HashSet<>();

        for (String word : keywords) {
            Matcher matcher = pattern.matcher(word);
            while (matcher.find()) {
                set.add(word.substring(matcher.start(), matcher.end()));
            }
        }

        List<String> result = new ArrayList<>();
        for (String word : set) {
            if (word.length() > 1) result.add(selectingRegisters.apply(word));
        }

        return result;
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
    private interface StrategySelectingRegisters {
        String apply(String word);

        StrategySelectingRegisters DEFAULT = new DefaultStrategySelectingRegisters();
        StrategySelectingRegisters UPPER = new UpperStrategySelectingRegisters();
        @SuppressWarnings("unused")
        StrategySelectingRegisters LOWER = new LowerStrategySelectingRegisters();

        class DefaultStrategySelectingRegisters implements StrategySelectingRegisters {
            @Override
            public String apply(@NotNull String word) {
                return word;
            }
        }


        class UpperStrategySelectingRegisters implements StrategySelectingRegisters {
            @Override
            public String apply(@NotNull String word) {
                return word.toUpperCase();
            }
        }

        class LowerStrategySelectingRegisters implements StrategySelectingRegisters {
            @Override
            public String apply(@NotNull String word) {
                return word.toLowerCase();
            }
        }
    }
}
