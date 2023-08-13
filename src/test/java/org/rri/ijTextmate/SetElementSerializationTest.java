package org.rri.ijTextmate;

import com.intellij.openapi.util.TextRange;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.Storage.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.Storage.PersistentStorage.SetElement;

import java.util.Collections;
import java.util.Map;

public class SetElementSerializationTest extends LightPlatformCodeInsightFixture4TestCase {
    private static String IDENTIFIER_STRATEGY = "";

    Map<String, SetElement> data = Map.of(
            "file1", createSetElement(new PlaceInjection("php", new TextRange(1, 10), IDENTIFIER_STRATEGY)),
            "file2", createSetElement(new PlaceInjection("cpp", new TextRange(1, 10), IDENTIFIER_STRATEGY),
                    new PlaceInjection("go", new TextRange(1, 10), IDENTIFIER_STRATEGY)),
            "file3", createSetElement(),
            "file4", createSetElement(),
            "file5", createSetElement(new PlaceInjection())
    );

    Map<String, SetElement> answer = Map.of(
            "file1", createSetElement(new PlaceInjection("php", new TextRange(1, 10), IDENTIFIER_STRATEGY)),
            "file2", createSetElement(new PlaceInjection("cpp", new TextRange(1, 10), IDENTIFIER_STRATEGY), new PlaceInjection("go", new TextRange(1, 10), IDENTIFIER_STRATEGY))
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

    private @NotNull SetElement createSetElement(PlaceInjection... placeInjections) {
        SetElement setElement = new SetElement();
        Collections.addAll(setElement, placeInjections);
        return setElement;
    }
}
