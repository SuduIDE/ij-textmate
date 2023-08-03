package org.rri.ijTextmate.Helpers;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.TextMateService;
import org.jetbrains.plugins.textmate.bundles.TextMateBundleReader;
import org.jetbrains.plugins.textmate.bundles.TextMateFileNameMatcher;
import org.jetbrains.plugins.textmate.bundles.TextMateGrammar;
import org.jetbrains.plugins.textmate.configuration.*;

import java.nio.file.Path;
import java.util.*;

@Service(Service.Level.PROJECT)
public final class TextMateHelper {
    private final Map<String, Path> languages = new HashMap<>();
    private final Map<String, String> languageToFileExtension = new HashMap<>(Map.of("textmate", ""));

    public TextMateHelper() {
        updateLanguages();
    }

    public void updateLanguages() {
        languages.clear();
        languages.put("textmate", Path.of(""));

        Map<String, TextMatePersistentBundle> userBundles = Objects.requireNonNull(TextMateUserBundlesSettings.getInstance()).getBundles();

        for (Map.Entry<String, TextMatePersistentBundle> entry : userBundles.entrySet()) {
            if (entry.getValue().getEnabled()) {
                languages.put(entry.getValue().getName(), Path.of(entry.getKey()));
            }
        }

        List<Path> builtinBundles = Objects.requireNonNull(TextMateBuiltinBundlesSettings.getInstance()).getBuiltinBundles();
        Set<String> offBundles = Objects.requireNonNull(TextMateBuiltinBundlesSettings.getInstance()).getTurnedOffBundleNames();

        for (Path path : builtinBundles) {
            TextMateBundleReader textMateBundleReader = TextMateService.getInstance().readBundle(path);
            if (textMateBundleReader == null || offBundles.contains(textMateBundleReader.getBundleName())) continue;
            languages.put(textMateBundleReader.getBundleName(), path);
        }
    }

    public List<String> getLanguages() {
        return languages.keySet().stream().toList();
    }

    public @NotNull Path getPath(String language) {
        return languages.get(language);
    }

    public @NotNull String getExtension(String language) {
        String fileExtension = languageToFileExtension.get(language);
        if (fileExtension != null) return fileExtension;

        synchronized (this) {
            fileExtension = languageToFileExtension.get(language);
            if (fileExtension == null) {
                fileExtension = calcExtension(getPath(language));

                languageToFileExtension.put(language, fileExtension);
            }
        }

        return fileExtension;
    }

    private @NotNull String calcExtension(@Nullable Path path) {
        TextMateBundleReader textMateBundleReader = TextMateService.getInstance().readBundle(path);
        if (textMateBundleReader == null) return "";

        Iterator<TextMateGrammar> iterator = textMateBundleReader.readGrammars().iterator();
        while (iterator.hasNext()) {
            TextMateGrammar grammar = iterator.next();
            for (TextMateFileNameMatcher fileNameMatcher : grammar.getFileNameMatchers()) {
                if (fileNameMatcher instanceof TextMateFileNameMatcher.Extension extension) {
                    return extension.getExtension();
                }
            }
        }

        return "";
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
