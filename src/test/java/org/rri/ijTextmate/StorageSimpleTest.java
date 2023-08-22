package org.rri.ijTextmate;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.rri.ijTextmate.Storage.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.Storage.TemporaryStorage.InjectionStrategies.SingleInjectionStrategy;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryMapPointerToPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryPlaceInjection;
import org.rri.ijTextmate.Storage.TemporaryStorage.TemporaryStorage;

import java.util.Objects;
import java.util.Set;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageSimpleTest extends LightPlatformCodeInsightFixture4TestCase {
    private final static String JAVA_FILE = "SimpleJavaCode.java";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/InjectionCases";
    }

    @Test
    public void test0() {
        myFixture.configureByFile(JAVA_FILE);
        Element element = PersistentStorage.getInstance(getProject()).getState();
        element.getChildren().clear();
    }

    @Test
    public void test1() {
        PsiFile psiFile = myFixture.configureByFile(JAVA_FILE);

        TemporaryMapPointerToPlaceInjection mapPointerToPlaceInjection = getMyMap(psiFile);

        var pointer = SmartPointerManager.createPointer(Objects.requireNonNull(InjectorHelper.findInjectionHost(100, psiFile)));
        mapPointerToPlaceInjection.put(new TemporaryPlaceInjection(pointer, "sql", new SingleInjectionStrategy()));

        PersistentStorage.getInstance(getProject()).getState();
    }

    @Test
    public void test2() {
        PsiFile psiFile = myFixture.configureByFile(JAVA_FILE);

        TemporaryMapPointerToPlaceInjection mapPointerToPlaceInjection = getMyMap(psiFile);

        var pointer = SmartPointerManager.createPointer(Objects.requireNonNull(InjectorHelper.findInjectionHost(156, psiFile)));
        mapPointerToPlaceInjection.put(new TemporaryPlaceInjection(pointer, "php", new SingleInjectionStrategy()));

        PersistentStorage.getInstance(getProject()).getState();
    }

    @Test
    public void test3() {
        PsiFile psiFile = myFixture.configureByFile(JAVA_FILE);

        TemporaryMapPointerToPlaceInjection mapPointerToPlaceInjection = getMyMap(psiFile);

        var pointer = SmartPointerManager.createPointer(Objects.requireNonNull(InjectorHelper.findInjectionHost(253, psiFile)));
        mapPointerToPlaceInjection.put(new TemporaryPlaceInjection(pointer, "go", new SingleInjectionStrategy()));

        PersistentStorage.getInstance(getProject()).getState();
    }

    @Test
    public void test4ContainsElements() {
        PsiFile psiFile = myFixture.configureByFile(JAVA_FILE);

        TemporaryMapPointerToPlaceInjection mapPointerToPlaceInjection = getMyMap(psiFile);

        assertTrue(intersectsWithElementFromMap(mapPointerToPlaceInjection.keySet(), 100));
        assertTrue(intersectsWithElementFromMap(mapPointerToPlaceInjection.keySet(), 156));
        assertTrue(intersectsWithElementFromMap(mapPointerToPlaceInjection.keySet(), 253));
        assertEquals(3, mapPointerToPlaceInjection.size());
    }

    private boolean intersectsWithElementFromMap(@NotNull Set<SmartPsiElementPointer<PsiLanguageInjectionHost>> keys, int offset) {
        TextRange textRange = new TextRange(offset, offset);
        for (var key : keys) {
            PsiElement element = key.getElement();
            if (element == null) continue;
            TextRange textRangeOfElement = element.getTextRange();
            if (textRangeOfElement != null && textRange.intersects(textRangeOfElement)) return true;
        }
        return false;
    }

    private @NotNull TemporaryMapPointerToPlaceInjection getMyMap(PsiFile psiFile) {
        String relativePath = getRelativePath(psiFile);

        return TemporaryStorage.getInstance(getProject()).get(relativePath);
    }

    private @NotNull String getRelativePath(PsiFile psiFile) {
        return InjectorHelper.getRelativePath(getProject(), psiFile);
    }
}
