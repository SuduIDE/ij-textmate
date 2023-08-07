package org.rri.ijTextmate;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToLanguage;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.Map;
import java.util.Objects;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageSimpleTest extends BasePlatformTestCase {
    private final static String FILE_NAME = "SimpleJavaCode.java";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    public void test0() {
        myFixture.configureByFile(FILE_NAME);
        PersistentStorage.MapFileToSetElement mapFileToSetElement = PersistentStorage.getInstance(getProject()).getState();
        mapFileToSetElement.clear();
    }

//    public void test1() {
//        PsiFile psiFile = myFixture.configureByFile(FILE_NAME);
//
//        TemporaryMapPointerToLanguage mapPointerToLanguage = getMyMap();
//
//        var pointer = SmartPointerManager.createPointer(Objects.requireNonNull(InjectorHelper.findInjectionHost(100, psiFile)));
//        mapPointerToLanguage.add(new TemporaryPlaceInjection(pointer, "sql"));
//    }

//    public void test2() {
//        PsiFile psiFile = myFixture.configureByFile(FILE_NAME);
//
//        TemporaryMapPointerToLanguage mapPointerToLanguage = getMyMap();
//
//        var pointer = SmartPointerManager.createPointer(Objects.requireNonNull(InjectorHelper.findInjectionHost(156, psiFile)));
//        mapPointerToLanguage.add(new TemporaryPlaceInjection(pointer, "php"));
//    }
//
    public void test3() {
        PsiFile psiFile = myFixture.configureByFile(FILE_NAME);

        TemporaryMapPointerToLanguage mapPointerToLanguage = getMyMap();

        var pointer = SmartPointerManager.createPointer(Objects.requireNonNull(InjectorHelper.findInjectionHost(253, psiFile)));
        mapPointerToLanguage.add(new TemporaryPlaceInjection(pointer, "go"));
    }

    public void test4ContainsElements() {
        myFixture.configureByFile(FILE_NAME);

        TemporaryMapPointerToLanguage mapPointerToLanguage = getMyMap();

//        assertTrue(intersectsWithElementFromMap(mapPointerToLanguage.getMap(), 100));
//        assertTrue(intersectsWithElementFromMap(mapPointerToLanguage.getMap(), 156));
        assertTrue(intersectsWithElementFromMap(mapPointerToLanguage.getMap(), 253));
        assertEquals(1, mapPointerToLanguage.getMap().size());
    }

    private boolean intersectsWithElementFromMap(@NotNull Map<SmartPsiElementPointer<PsiLanguageInjectionHost>, String> map, int offset) {
        TextRange textRange = new TextRange(offset, offset);
        for (var key : map.keySet()) {
            PsiElement element = key.getElement();
            if (element == null) continue;
            TextRange textRangeOfElement = element.getTextRange();
            if (textRangeOfElement != null && textRange.intersects(textRangeOfElement)) return true;
        }
        return false;
    }

    private @NotNull TemporaryMapPointerToLanguage getMyMap() {
        String relativePath = getRelativePath();

        return TemporaryStorage.getInstance(getProject()).get(relativePath);
    }

    private @NotNull String getRelativePath() {
        PsiFile psiFile = myFixture.configureByFile(FILE_NAME);
        return InjectorHelper.getRelativePath(getProject(), psiFile);
    }
}
