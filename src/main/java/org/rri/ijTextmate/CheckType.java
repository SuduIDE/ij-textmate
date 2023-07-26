package org.rri.ijTextmate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.rri.ijTextmate.Helpers.InjectorHelper;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.PersistentStorage.LanguageID;
import org.rri.ijTextmate.PersistentStorage.PersistentStorage;
import org.rri.ijTextmate.PersistentStorage.PlaceInjection;
import org.rri.ijTextmate.PersistentStorage.SetElement;

import static java.lang.System.out;

public class CheckType extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null && editor != null && file != null;

        SetElement element = new SetElement();
        element.add(new PlaceInjection("sql", 20, new TextRange(1, 102)));
        element.add(new PlaceInjection("php", 24, new TextRange(1, 102)));
        element.add(new PlaceInjection("cpp", 48, new TextRange(1, 102)));

        PersistentStorage.MapFileToSetElement mapFileToSetElement = new PersistentStorage.MapFileToSetElement();
        mapFileToSetElement.put("key", element);
        mapFileToSetElement.put("key3", element);
        PersistentStorage.getInstance(project).loadState(mapFileToSetElement);

//        String basePath = project.getBasePath();
//
//        project.getBasePath();
//        file.getVirtualFile().getPath();

        //        var x = new PersistentStorage.SetElement();
        //        x.addElement(new TemporaryPlace("lang", 1));
        //        project.getService(PersistentStorage.class).loadState(x);
        //        project.getService(TemporaryPlace.class).loadState(new TemporaryPlace("", 124124));
        //        Collection<PsiReference> refs;
        //        ReferencesSearch.search(file.findElementAt(editor.getCaretModel().getOffset()));
        //        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        //        System.out.println(element.getClass());
        //        System.out.println(element.getText());
        //        System.out.println(element.getContext());

        //        var x = InjectorHelper.findInjectionHost(editor, file).getUserData(MyTemporaryLanguageInjectionSupport.MY_TEMPORARY_INJECTED_LANGUAGE);
        //        if (x != null)
        //        System.out.println(x.getSuffix());

        //        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        //        out.println();
        //        assert element.getParent().getParent().getFirstChild().getReference() != null;
        //        element = element.getParent().getParent().getFirstChild().getReference().resolve();
        //        assert element != null;
        //        if (element instanceof PsiReference) {
        //            element = element.getParent();
        //        }
        //        assert element != null;
        //        element = element.getReference().resolve();
        //        element.getParent() instanceof PsiLiteralValue
        //        element.getParent().getParent() instanceof PsiNameIdentifierOwner
        //        ReferencesSearch.search(element).findAll()

        //        element.getParent() instanceof PsiLiteralValue
        //        element.getParent().getParent().getChildren()[0] instanceof PsiReference
        //        element.getParent().getParent().getChildren()[0].getReference().resolve() instanceof PsiNameIdentifierOwner
        //        ReferencesSearch.search(element).findAll()
        //        out.println(element.getText());
        //        Arrays.stream(element.getReferences()).forEach(x -> out.println(x.getElement().getText()));
        //        var x = Objects.requireNonNull(InjectorHelper.findInjectionHost(editor, file)).getUserData(InjectLanguage.MY_TEMPORARY_INJECTED_LANGUAGE);
        //        if (x != null)
        //            System.out.println(x.getSuffix());
//        PsiLanguageInjectionHost host = InjectorHelper.findInjectionHost(editor, file);
//        if (host == null) return;
//        LanguageID l = host.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE);
//        if (l == null) {
//            out.println("Nullable");
//            return;
//        }
//        out.println(l.getID());
    }
}
