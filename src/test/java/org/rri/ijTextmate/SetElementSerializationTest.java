package org.rri.ijTextmate;

import com.intellij.openapi.util.TextRange;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.PersistentStorage.SetElement;

import java.util.Collections;
import java.util.Map;

public class SetElementSerializationTest extends LightJavaCodeInsightFixtureTestCase {
    Map<String, SetElement> map = Map.of(
            "file1", createSetElement(new PlaceInjection("php", 15, new TextRange(1, 10))),
            "file2", createSetElement(new PlaceInjection("cpp", 68, new TextRange(1, 10)), new PlaceInjection("go", 79, new TextRange(1, 10))),
            "file3", createSetElement(),
            "file4", createSetElement(),
            "file5", createSetElement(new PlaceInjection())
    );

    Map<String, SetElement> answer = Map.of(
            "file1", createSetElement(new PlaceInjection("php", 15, new TextRange(1, 10))),
            "file2", createSetElement(new PlaceInjection("cpp", 68, new TextRange(1, 10)), new PlaceInjection("go", 79, new TextRange(1, 10)))
    );

    public void testSerialization() {
        PersistentStorage.ConverterMapFileToSetElement converterMapFileToSetElement = new PersistentStorage.ConverterMapFileToSetElement();
        String json = converterMapFileToSetElement.toString(map);
        assertNotNull(json);

        Map<String, SetElement> newMap = converterMapFileToSetElement.fromString(json);
        assertEquals(answer, newMap);
    }

    private @NotNull SetElement createSetElement(PlaceInjection... placeInjections) {
        SetElement setElement = new SetElement();
        Collections.addAll(setElement, placeInjections);
        return setElement;
    }
}
