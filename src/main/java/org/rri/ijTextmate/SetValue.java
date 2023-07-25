package org.rri.ijTextmate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValuesManager;
import org.jetbrains.annotations.NotNull;
import org.rri.ijTextmate.Helpers.InjectorHelper;

import static java.lang.System.out;

public class SetValue extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null && editor != null && file != null;

        PsiElement psiElement = InjectorHelper.findInjectionHost(editor, file);
        PsiCachedValue cachedValue = new PsiCachedValue(psiElement);
        String ans = CachedValuesManager.getCachedValue(psiElement, cachedValue.getProvider());
        out.println(ans);
//        FileContentUtil.reparseFiles(project, List.of(file.getVirtualFile()), false);
//        SetElement element = new SetElement();
//        element.add(new PlaceInjection("sql", 20));
//        element.add(new PlaceInjection("php", 24));
//        element.add(new PlaceInjection("cpp", 48));
//
//        PersistentStorage.MapFileToSetElement mapFileToSetElement = new PersistentStorage.MapFileToSetElement();
//        mapFileToSetElement.put("key", element);
//        mapFileToSetElement.put("key3", element);
//        PersistentStorage.getInstance(project).loadState(mapFileToSetElement);
//
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
