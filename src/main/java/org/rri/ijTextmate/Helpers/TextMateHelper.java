package org.rri.ijTextmate.Helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateService;
import org.jetbrains.plugins.textmate.bundles.TextMateBundleReader;
import org.jetbrains.plugins.textmate.configuration.*;

import java.nio.file.Path;
import java.util.*;

@Service(Service.Level.PROJECT)
public final class TextMateHelper {
    private final Map<String, String> languages = new HashMap<>();
    private final Map<String, String> languageToFileExtension = new HashMap<>();
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
        languages.put("textmate", "");

        Map<String, TextMatePersistentBundle> userBundles = Objects.requireNonNull(TextMateUserBundlesSettings.getInstance()).getBundles();

        for (Map.Entry<String, TextMatePersistentBundle> entry : userBundles.entrySet()) {
            languages.put(entry.getValue().getName(), entry.getKey());
        }

        List<Path> builtinBundles = Objects.requireNonNull(TextMateBuiltinBundlesSettings.getInstance()).getBuiltinBundles();
        Set<String> offBundles = Objects.requireNonNull(TextMateBuiltinBundlesSettings.getInstance()).getTurnedOffBundleNames();

        for (Path path : builtinBundles) {
            TextMateBundleReader textMateBundleReader = TextMateService.getInstance().readBundle(path);
            if (textMateBundleReader == null || offBundles.contains(textMateBundleReader.getBundleName())) continue;
            languages.put(textMateBundleReader.getBundleName(), path.toString());
        }
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

        Path path = getPath(language).resolve(FILE_WITH_EXTENSION);
        try {
            String text = FileUtils.readFileToString(path.toFile(), UTF8);
            JsonElement root = JsonParser.parseString(text);

            fileExtension = root.getAsJsonObject().get(CONTRIBUTES)
                    .getAsJsonObject().get(LANGUAGES)
                    .getAsJsonArray().get(0)
                    .getAsJsonObject().get(EXTENSIONS)
                    .getAsJsonArray().get(0).getAsString();
            if (fileExtension.startsWith(".")) fileExtension = fileExtension.substring(1);
        } catch (Throwable ignore) {
            fileExtension = "";
        }

        languageToFileExtension.put(language, fileExtension);

        return fileExtension;
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
