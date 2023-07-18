package com.razerford.ijTextmate.Helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.configuration.BundleConfigBean;
import org.jetbrains.plugins.textmate.configuration.TextMateSettings;

import java.nio.file.Path;
import java.util.*;

@Service(Service.Level.PROJECT)
public final class TextMateHelper {
    private final Map<String, String> languages = new HashMap<>();
    private static final Path FILE_WITH_EXTENSION = Path.of("package.json");
    private static final String CONTRIBUTES = "contributes";
    private static final String LANGUAGES = "languages";
    private static final String EXTENSIONS = "extensions";
    private static final String UTF8 = "UTF8";

    public TextMateHelper() {
        updateLanguages();
    }

    public void updateLanguages() {
        languages.clear();
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
        Path path = getPath(language).resolve(FILE_WITH_EXTENSION);
        try {
            String text = FileUtils.readFileToString(path.toFile(), UTF8);
            JsonElement root = JsonParser.parseString(text);
            String extension = root.getAsJsonObject().get(CONTRIBUTES)
                    .getAsJsonObject().get(LANGUAGES)
                    .getAsJsonArray().get(0)
                    .getAsJsonObject().get(EXTENSIONS)
                    .getAsJsonArray().get(0).toString();
            if (extension.length() > 3) return extension.substring(2, extension.length() - 1);
        } catch (Throwable ignore) {
        }
        return "";
    }

    public static @NotNull TextMateHelper upateLanguagesAndGetTextMateHelper(@NotNull Project project) {
        TextMateHelper textMateHelper = project.getService(TextMateHelper.class);
        textMateHelper.updateLanguages();
        return textMateHelper;
    }
}
