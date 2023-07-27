package org.rri.ijTextmate;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.PersistentStorage.SetElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistentStorageSimpleTest extends BasePlatformTestCase {
    private final static String TEXT_PROGRAM = "";
    private final static String FILE_NAME = "test.java";

    public void test0() {
        myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        PersistentStorage.MapFileToSetElement mapFileToSetElement = PersistentStorage.getInstance(getProject()).getState();
        mapFileToSetElement.clear();
    }

    public void test1() {
        String relativePath = getRelativePath();

        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("sql", new TextRange(1, 10)));
    }

    public void test2() {
        String relativePath = getRelativePath();

        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("python", new TextRange(40, 43)));
    }

    public void test3() {
        String relativePath = getRelativePath();

        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("cpp", new TextRange(143, 145)));
    }

    public void test4() {
        String relativePath = getRelativePath();

        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("php", new TextRange(85, 88)));
    }

    public void test5() {
        String relativePath = getRelativePath();

        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("java", new TextRange(11, 13)));
    }

    public void test6PutPlaceInjectionIntersects() {
        String relativePath = getRelativePath();

        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);

        final int expectedSize = setElement.size();
        setElement.add(new PlaceInjection("java", new TextRange(11, 13)));

        String message = "Elements with moving TextRange replaces are equivalent";
        assertEquals(message, expectedSize, setElement.size());
    }

    public void test7ContainsElements() {
        String relativePath = getRelativePath();

        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);

        assertTrue(setElement.contains(5));
        assertTrue(setElement.contains(40));
        assertTrue(setElement.contains(145));
        assertTrue(setElement.contains(85));
        assertTrue(setElement.contains(13));
    }

    public void test8RemoveElements() {
        String relativePath = getRelativePath();

        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);

        assertTrue(setElement.remove(5));
        assertTrue(setElement.remove(40));
        assertTrue(setElement.remove(145));
        assertTrue(setElement.remove(new PlaceInjection("php", new TextRange(85, 88))));
        assertTrue(setElement.remove(new PlaceInjection("java", new TextRange(11, 13))));

        assertEquals(0, setElement.size());
    }

    private @NotNull String getRelativePath() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        return InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
    }
}
