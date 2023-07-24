package org.rri.ijTextmate;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.PersistentStorage.SetElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistentStorageSimpleTest extends BasePlatformTestCase {
    private final static String TEXT_PROGRAM = "";
    private final static String FILE_NAME = "test.java";

    @Test
    public void test0() {
        myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        PersistentStorage.MapFileToSetElement mapFileToSetElement = PersistentStorage.getInstance(getProject()).getState();
        mapFileToSetElement.clear();
    }

    @Test
    public void test1() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("sql", 3));
    }

    @Test
    public void test2() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("python", 42));
    }

    @Test
    public void test3() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("cpp", 144));
    }

    @Test
    public void test4() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("php", 87));
    }

    @Test
    public void test5() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("java", 12));
    }

    @Test
    public void test6CheckElements() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.remove(new PlaceInjection("sql", 3));
        setElement.remove(new PlaceInjection("python", 42));
        setElement.remove(new PlaceInjection("cpp", 144));
        setElement.remove(new PlaceInjection("php", 87));
        setElement.remove(new PlaceInjection("java", 12));
        assertEquals(0, setElement.size());
    }
}
