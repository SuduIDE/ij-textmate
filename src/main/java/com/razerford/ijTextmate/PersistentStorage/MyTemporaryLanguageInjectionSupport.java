package com.razerford.ijTextmate.PersistentStorage;

import com.intellij.openapi.util.Key;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;

public class MyTemporaryLanguageInjectionSupport {
    public static final Key<InjectedLanguage> MY_TEMPORARY_INJECTED_LANGUAGE = Key.create("MY_TEMPORARY_INJECTED_LANGUAGE");
}
