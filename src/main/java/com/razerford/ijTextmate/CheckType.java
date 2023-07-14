package com.razerford.ijTextmate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.razerford.ijTextmate.Helpers.InjectorHelper;
import com.razerford.ijTextmate.TemporaryEntity.MyTemporaryLanguageInjectionSupport;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CheckType extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null && editor != null && file != null;
//        Collection<PsiReference> refs;
//        ReferencesSearch.search(file.findElementAt(editor.getCaretModel().getOffset()));
//        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
//        System.out.println(element.getClass());
//        System.out.println(element.getText());
//        System.out.println(element.getContext());
        var x = InjectorHelper.findInjectionHost(editor, file).getUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE);
        System.out.println(x.getID());
    }
}
