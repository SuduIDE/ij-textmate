package org.rri.ijTextmate.Helpers;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.configuration.*;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractedLanguageInformation;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.InformationExtractor;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.InformationExtractorImpl;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.nio.file.Path;
import java.util.*;

@Service(Service.Level.PROJECT)
public final class TextMateHelper {
    private final Map<String, String> languages = new HashMap<>();
    private final Map<String, String> languageToFileExtension = new HashMap<>(Map.of("textmate", ""));
    private final Map<String, ExtractedLanguageInformation> languageToInformation = new HashMap<>(Map.of("textmate", new ExtractedLanguageInformation()));
    private final Map<String, SelectingRegistersStrategy> languageToStrategyRegister = new HashMap<>(Map.of("docker", SelectingRegistersStrategy.UPPER, "sql", SelectingRegistersStrategy.UPPER));
    private final Map<String, AbstractFileType> extensionToFileType = new HashMap<>();

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
                InformationExtractor informationExtractor = new InformationExtractorImpl(language, getPath(language), getStrategy(language), extensionToFileType);

                fileExtension = informationExtractor.getExtension();
                languageToFileExtension.put(language, fileExtension);

                ReadAction.run(() -> languageToInformation.put(language, informationExtractor.getExtractedLanguageInformation()));
            }
        }

        return fileExtension;
    }

    public @NotNull ExtractedLanguageInformation getInformation(String language) {
        return languageToInformation.get(language);
    }

    private SelectingRegistersStrategy getStrategy(String language) {
        SelectingRegistersStrategy strategy = languageToStrategyRegister.get(language);
        if (strategy != null) return strategy;
        return SelectingRegistersStrategy.DEFAULT;
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
