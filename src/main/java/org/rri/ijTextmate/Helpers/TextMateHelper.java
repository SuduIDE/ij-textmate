package org.rri.ijTextmate.Helpers;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.TextMateService;
import org.jetbrains.plugins.textmate.bundles.TextMateBundleReader;
import org.jetbrains.plugins.textmate.bundles.TextMateFileNameMatcher;
import org.jetbrains.plugins.textmate.bundles.TextMateGrammar;
import org.jetbrains.plugins.textmate.configuration.*;
import org.jetbrains.plugins.textmate.plist.PListValue;
import org.jetbrains.plugins.textmate.plist.Plist;
import org.jetbrains.plugins.textmate.plist.PlistValueType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Path;
import java.util.*;

@Service(Service.Level.PROJECT)
public final class TextMateHelper {
    private final Map<String, Path> languages = new HashMap<>();
    private final Map<String, String> languageToFileExtension = new HashMap<>(Map.of("textmate", ""));
    private final Map<String, List<String>> languageToKeywords = new HashMap<>(Map.of("textmate", Collections.emptyList()));
    private final static String MATCH = "match";
    private final static String PATTERNS = "patterns";
    private final static String REPOSITORY = "repository";

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
                fileExtension = getExtension(getPath(language));

                languageToFileExtension.put(language, fileExtension);

                ReadAction.run(() -> languageToKeywords.put(language, calcKeywords(getPath(language))));
            }
        }

        return fileExtension;
    }

    public @NotNull List<String> getKeywords(String language) {
        return languageToKeywords.get(language);
    }

    private @NotNull String getExtension(@Nullable Path path) {
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

    private @NotNull List<String> calcKeywords(@Nullable Path path) {
        ArrayList<String> keywords = new ArrayList<>();
        if (path == null) return keywords;

        TextMateBundleReader textMateBundleReader = TextMateService.getInstance().readBundle(path);
        if (textMateBundleReader == null) return keywords;

        Iterator<TextMateGrammar> textMateGrammarIterator = textMateBundleReader.readGrammars().iterator();
        while (textMateGrammarIterator.hasNext()) {
            Plist plist = textMateGrammarIterator.next().getPlist().getValue();

            PListValue pListValue = plist.getPlistValue(PATTERNS);
            if (pListValue != null) recursiveExtraction(pListValue, keywords);

            pListValue = plist.getPlistValue(REPOSITORY);
            if (pListValue != null) recursiveExtraction(pListValue, keywords);
        }

        return splitRegex(keywords);
    }

    private void recursiveExtraction(@NotNull PListValue pListValue, @NotNull ArrayList<String> values) {
        if (PlistValueType.ARRAY.equals(pListValue.getType())) {
            for (PListValue value : pListValue.getArray()) {
                recursiveExtraction(value, values);
            }
        }
        if (!PlistValueType.DICT.equals(pListValue.getType())) return;
        PListValue value = pListValue.getPlist().getPlistValue(MATCH);
        if (value != null) {
            String regex = value.getString();
            values.add(regex);
            return;
        }
        for (Map.Entry<String, PListValue> entry : pListValue.getPlist().entries()) {
            recursiveExtraction(entry.getValue(), values);
        }
    }

    private @NotNull List<String> splitRegex(@NotNull List<String> keywords) {
        Pattern pattern = Pattern.compile("([a-z]+[a-z_]*)", Pattern.CASE_INSENSITIVE);
        Set<String> set = new HashSet<>();

        for (String word : keywords) {
            Matcher matcher = pattern.matcher(word);
            while (matcher.find()) {
                set.add(word.substring(matcher.start(), matcher.end()));
            }
        }

        List<String> result = new ArrayList<>();
        for (String word : set) {
            if (word.length() > 1) result.add(word);
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
}
