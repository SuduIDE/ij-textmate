package com.razerford.ijTextmate.Inject;

import com.intellij.openapi.util.Key;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;

public class LanguageInjectionSupport {
    public static final Key<InjectedLanguage> KEY_TEMPORARY_INJECTED_LANGUAGE = Key.create("KEY_TEMPORARY_INJECTED_LANGUAGE");
}
