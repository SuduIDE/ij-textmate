package org.rri.ijTextmate;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistentStorageSimpleTest extends BasePlatformTestCase {
    @Test
    public void test0() {
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(getProject()).getState();
        elements.clear();
    }

    @Test
    public void test1() {
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(getProject()).getState();
        elements.add(new PlaceInjection("sql", 3));
    }

    @Test
    public void test2() {
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(getProject()).getState();
        elements.add(new PlaceInjection("python", 42));
    }

    @Test
    public void test3() {
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(getProject()).getState();
        elements.add(new PlaceInjection("cpp", 144));
    }

    @Test
    public void test4() {
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(getProject()).getState();
        elements.add(new PlaceInjection("php", 87));
    }

    @Test
    public void test5() {
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(getProject()).getState();
        elements.add(new PlaceInjection("java", 12));
    }

    @Test
    public void test6CheckElements() {
        PersistentStorage.SetElement elements = PersistentStorage.getInstance(getProject()).getState();
        elements.remove(new PlaceInjection("sql", 3));
        elements.remove(new PlaceInjection("python", 42));
        elements.remove(new PlaceInjection("cpp", 144));
        elements.remove(new PlaceInjection("php", 87));
        elements.remove(new PlaceInjection("java", 12));
        assertEquals(0, elements.size());
    }
}
