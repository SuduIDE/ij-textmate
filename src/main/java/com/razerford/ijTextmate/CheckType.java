package com.razerford.ijTextmate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import com.razerford.ijTextmate.Inject.InjectLanguage;
import com.razerford.ijTextmate.PersistentStorage.PersistentStorage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import static java.lang.System.out;

public class CheckType extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null && editor != null && file != null;
        for (var ee : project.getService(PersistentStorage.class).getState().getElements()) {
            out.println(ee.getElement().getText());
        }
//        Collection<PsiReference> refs;
//        ReferencesSearch.search(file.findElementAt(editor.getCaretModel().getOffset()));
//        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
//        System.out.println(element.getClass());
//        System.out.println(element.getText());
//        System.out.println(element.getContext());

//        var x = InjectorHelper.findInjectionHost(editor, file).getUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE);
//        if (x != null)
//        System.out.println(x.getSuffix());

        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        out.println();
//        assert element.getParent().getParent().getFirstChild().getReference() != null;
//        element = element.getParent().getParent().getFirstChild().getReference().resolve();
        assert element != null;
        if (element instanceof PsiReference) {
            element = element.getParent();
        }
        assert element != null;
        element = element.getReference().resolve();
//        element.getParent() instanceof PsiLiteralValue
//        element.getParent().getParent() instanceof PsiNameIdentifierOwner
//        ReferencesSearch.search(element).findAll()

//        element.getParent() instanceof PsiLiteralValue
//        element.getParent().getParent().getChildren()[0] instanceof PsiReference
//        element.getParent().getParent().getChildren()[0].getReference().resolve() instanceof PsiNameIdentifierOwner
//        ReferencesSearch.search(element).findAll()
        out.println(element.getText());
        Arrays.stream(element.getReferences()).forEach(x -> out.println(x.getElement().getText()));
        var x = Objects.requireNonNull(InjectorHelper.findInjectionHost(editor, file)).getUserData(InjectLanguage.MY_TEMPORARY_INJECTED_LANGUAGE);
//        if (x != null)
//            System.out.println(x.getSuffix());
    }
}
