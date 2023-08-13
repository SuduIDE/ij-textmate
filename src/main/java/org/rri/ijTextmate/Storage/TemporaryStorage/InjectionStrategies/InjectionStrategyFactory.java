package org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class InjectionStrategyFactory {
    private static final Map<String, InjectionStrategy> identifierToInjectionStrategy = getMap();

    public static InjectionStrategy create(String identifier) {
        return identifierToInjectionStrategy.get(identifier);
    }

    private static @NotNull Map<String, InjectionStrategy> getMap() {
        Map<String, InjectionStrategy> map = new HashMap<>();

        InjectionStrategy rootMultiple = new RootMultipleInjectionStrategy();
        InjectionStrategy single = new SingleInjectionStrategy();

        map.put(rootMultiple.identifier(), rootMultiple);
        map.put(single.identifier(), single);

        return map;
    }
}
