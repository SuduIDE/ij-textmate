package org.rri.ijTextmate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

/*
 * This action is for tests only.
 */

public class CheckType extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null && editor != null && file != null;
        PsiElement psiElement = file.findElementAt(editor.getCaretModel().getOffset());
        assert psiElement != null;
        var query = ReferencesSearch.search(psiElement, GlobalSearchScope.allScope(project)).findAll();

//        new ArrayList<>(ReferencesSearch.search(psiElement.getParent().getParent().getChildren()[0]).findAll()).get(2).getElement().getParent().getChildren()[1].getOriginalElement() instanceof PsiLanguageInjectionHost

//        ReferencesSearch.search(psiElement.getParent()).findAll();

//        ReferencesSearch.search(psiElement.getParent().getReferences()[0].resolve()).findAll()

//        FileContentUtil.reparseFiles(project, List.of(file.getVirtualFile()), true);

//        Supplier<String> supplier = () -> {
//            System.out.println("supplier");
//            return "";
//        };
//        System.out.println(new Object().hashCode());
//        System.out.println(file.getUserData(Constants.MY_TEMPORARY_INJECTED_LANGUAGE));
//        System.out.println(CacheListTemporaryPlaceInjection.getInstance(project).getSimpleModificationTracker().getModificationCount());

//        SetElement element = new SetElement();
//        element.add(new PlaceInjection("sql", new TextRange(1, 102)));
//        element.add(new PlaceInjection("php", new TextRange(1, 102)));
//        element.add(new PlaceInjection("cpp", new TextRange(1, 102)));
//
//        PersistentStorage.MapFileToSetElement mapFileToSetElement = new PersistentStorage.MapFileToSetElement();
//        mapFileToSetElement.put("key", element);
//        mapFileToSetElement.put("key3", element);
//        PersistentStorage.getInstance(project).loadState(mapFileToSetElement);

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
