package org.rri.ijTextmate.Helpers.LanguageInformationExtractor;

import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.bundles.Bundle;
import org.jetbrains.plugins.textmate.bundles.BundleFactory;
import org.jetbrains.plugins.textmate.plist.CompositePlistReader;
import org.jetbrains.plugins.textmate.plist.Plist;
import org.jetbrains.plugins.textmate.plist.PlistReader;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractorInformationFromSource.ExtractorInformationFromSource;
import org.rri.ijTextmate.Helpers.LanguageInformationExtractor.ExtractorInformationFromSource.ExtractorInformationFromTextMate;
import org.rri.ijTextmate.Helpers.SelectingRegistersStrategy.SelectingRegistersStrategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class InformationExtractorImpl implements InformationExtractor {
    private final String language;
    private final Path pathToBundle;
    private final SelectingRegistersStrategy registersStrategy;
    private String extension;
    private List<String> extensions;
    private ExtractedLanguageInformation languageInformation;
    private final Map<String, AbstractFileType> extensionToFileType;
    private static final PlistReader plistReader = new CompositePlistReader();
    private static final BundleFactory bundleFactory = new BundleFactory(plistReader);

    public InformationExtractorImpl(String language, Path pathToBundle, SelectingRegistersStrategy registersStrategy, Map<String, AbstractFileType> extensionToFileType) {
        this.language = language;
        this.pathToBundle = pathToBundle;
        this.registersStrategy = registersStrategy;
        this.extensionToFileType = extensionToFileType;
    }

    @Override
    public String getExtension() {
        if (extension != null) return extension;
        extension = calcExtension(getOrCalcExtensions(pathToBundle), language);
        return extension;
    }

    @Override
    public ExtractedLanguageInformation getExtractedLanguageInformation() {
        if (languageInformation != null) return languageInformation;
        languageInformation = calcKeywords(getOrCalcExtensions(pathToBundle), language, extensionToFileType);
        return languageInformation;
    }

    private @NotNull String calcExtension(@NotNull List<String> extensions, @NotNull String language) {
        if (extensions.contains(language.toLowerCase())) return language.toLowerCase();
        if (extensions.isEmpty()) return "";
        return extensions.get(0);
    }

    private @NotNull List<String> getOrCalcExtensions(@NotNull Path path) {
        if (extensions != null) return extensions;

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
        extensions = allExtension;
        return allExtension;
    }

    private @Nullable Bundle createBundle(Path path) {
        try {
            return bundleFactory.fromDirectory(path.toFile());
        } catch (Throwable ignored) {
        }
        return null;
    }

    private @NotNull ExtractedLanguageInformation calcKeywords(@NotNull List<String> extensions, @NotNull String language) {
        return new ExtractorInformationFromTextMate(language, extensions, registersStrategy).extract();
    }

    @Contract("_, _, _ -> new")
    private @NotNull ExtractedLanguageInformation calcKeywords(@NotNull List<String> extensions, @NotNull String language, Map<String, AbstractFileType> extensionToFileType) {
        return ExtractorInformationFromSource.create(extensions, language, extensionToFileType, registersStrategy).extract();
    }
}
