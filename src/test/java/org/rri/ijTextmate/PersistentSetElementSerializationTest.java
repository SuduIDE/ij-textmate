package org.rri.ijTextmate;

import com.intellij.openapi.util.TextRange;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentPlaceInjection;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentSetElement;

import java.util.Collections;
import java.util.Map;

public class PersistentSetElementSerializationTest extends LightPlatformCodeInsightFixture4TestCase {
    private static final String IDENTIFIER_STRATEGY = "SingleInjectionStrategy";

    Map<String, PersistentSetElement> data = Map.of(
            "file1", createSetElement(new PersistentPlaceInjection("php", new TextRange(1, 10), IDENTIFIER_STRATEGY)),
            "file2", createSetElement(new PersistentPlaceInjection("cpp", new TextRange(1, 10), IDENTIFIER_STRATEGY),
                    new PersistentPlaceInjection("go", new TextRange(1, 10), IDENTIFIER_STRATEGY)),
            "file3", createSetElement(),
            "file4", createSetElement(),
            "file5", createSetElement(new PersistentPlaceInjection())
    );

    Map<String, PersistentSetElement> answer = Map.of(
            "file1", createSetElement(new PersistentPlaceInjection("php", new TextRange(1, 10), IDENTIFIER_STRATEGY)),
            "file2", createSetElement(new PersistentPlaceInjection("cpp", new TextRange(1, 10), IDENTIFIER_STRATEGY), new PersistentPlaceInjection("go", new TextRange(1, 10), IDENTIFIER_STRATEGY))
    );

    @Test
    public void testSerialization() {
        PersistentStorage.MapFileToSetElement map = new PersistentStorage.MapFileToSetElement(data);
        Element element = map.toElement();
        assertNotNull(element);

        PersistentStorage.MapFileToSetElement newMap = new PersistentStorage.MapFileToSetElement();
        newMap.fromElement(element);

        assertEquals(answer, newMap.getMap());
    }

    private @NotNull PersistentSetElement createSetElement(PersistentPlaceInjection... persistentPlaceInjections) {
        PersistentSetElement persistentSetElement = new PersistentSetElement();
        Collections.addAll(persistentSetElement, persistentPlaceInjections);
        return persistentSetElement;
    }
}
