package org.rri.ijTextmate;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
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
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("sql", 3, new TextRange(1, 10)));
    }

    public void test2() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("python", 42, new TextRange(1, 10)));
    }

    public void test3() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("cpp", 144, new TextRange(1, 10)));
    }

    public void test4() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("php", 87, new TextRange(1, 10)));
    }

    public void test5() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();
        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);
        setElement.add(new PlaceInjection("java", 12, new TextRange(1, 10)));
    }

    public void test6CheckElements() {
        PsiFile psiFile = myFixture.configureByText(FILE_NAME, TEXT_PROGRAM);
        String relativePath = InjectorHelper.gitRelativePath(getProject(), psiFile).toString();

        SetElement setElement = PersistentStorage.getInstance(getProject()).getState().get(relativePath);

        setElement.remove(new PlaceInjection("sql", 3, new TextRange(1, 10)));
        setElement.remove(new PlaceInjection("python", 42, new TextRange(1, 10)));
        setElement.remove(new PlaceInjection("cpp", 144, new TextRange(1, 10)));
        setElement.remove(new PlaceInjection("php", 87, new TextRange(1, 10)));
        setElement.remove(new PlaceInjection("java", 12, new TextRange(1, 10)));

        assertEquals(0, setElement.size());
    }
}
