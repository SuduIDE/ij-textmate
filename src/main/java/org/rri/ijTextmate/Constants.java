package org.rri.ijTextmate;

import com.intellij.openapi.util.Key;
import com.intellij.psi.util.CachedValue;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;

public class Constants {
    public static final Key<TemporaryPlaceInjection> MY_TEMPORARY_INJECTED_LANGUAGE = Key.create("MY_TEMPORARY_INJECTED_LANGUAGE");
    public static final Key<CachedValue<TemporaryPlaceInjection>> MY_CACHED_TEMPORARY_INJECTED_LANGUAGE = Key.create("MY_CACHED_TEMPORARY_INJECTED_LANGUAGE");
    public static final Key<Object> MY_LANGUAGE_INJECTED = Key.create("MY_LANGUAGE_INJECTED");
}
