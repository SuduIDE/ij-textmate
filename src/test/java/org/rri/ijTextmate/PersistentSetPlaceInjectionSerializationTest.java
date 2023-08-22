package org.rri.ijTextmate;

import com.intellij.openapi.util.TextRange;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentPlaceInjection;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentSetPlaceInjection;

import java.util.Collections;
import java.util.Map;

public class PersistentSetPlaceInjectionSerializationTest extends LightPlatformCodeInsightFixture4TestCase {
    private static final String IDENTIFIER_STRATEGY = "SingleInjectionStrategy";

    Map<String, PersistentSetPlaceInjection> data = Map.of(
            "file1", createSetPlaceInjection(new PersistentPlaceInjection("php", new TextRange(1, 10), IDENTIFIER_STRATEGY)),
            "file2", createSetPlaceInjection(new PersistentPlaceInjection("cpp", new TextRange(1, 10), IDENTIFIER_STRATEGY),
                    new PersistentPlaceInjection("go", new TextRange(1, 10), IDENTIFIER_STRATEGY)),
            "file3", createSetPlaceInjection(),
            "file4", createSetPlaceInjection(),
            "file5", createSetPlaceInjection(new PersistentPlaceInjection())
    );

    Map<String, PersistentSetPlaceInjection> answer = Map.of(
            "file1", createSetPlaceInjection(new PersistentPlaceInjection("php", new TextRange(1, 10), IDENTIFIER_STRATEGY)),
            "file2", createSetPlaceInjection(new PersistentPlaceInjection("cpp", new TextRange(1, 10), IDENTIFIER_STRATEGY), new PersistentPlaceInjection("go", new TextRange(1, 10), IDENTIFIER_STRATEGY))
    );

    @Test
    public void testSerialization() {
        PersistentStorage.MapFileToSetElement map = new PersistentStorage.MapFileToSetElement(data);
        Element element = map.toElement();
        assertNotNull(element);

        PersistentStorage.MapFileToSetElement newMap = new PersistentStorage.MapFileToSetElement();
        newMap.fromElement(element);

        assertEquals(answer.entrySet(), newMap.entrySet());
    }

    private @NotNull PersistentSetPlaceInjection createSetPlaceInjection(PersistentPlaceInjection... persistentPlaceInjections) {
        PersistentSetPlaceInjection persistentSetPlaceInjection = new PersistentSetPlaceInjection();
        Collections.addAll(persistentSetPlaceInjection, persistentPlaceInjections);
        return persistentSetPlaceInjection;
    }
}
