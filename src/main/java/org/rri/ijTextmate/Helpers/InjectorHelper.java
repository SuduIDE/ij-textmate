package org.rri.ijTextmate.Helpers;

import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;

public class InjectorHelper {
    @Nullable
    public static PsiLanguageInjectionHost findInjectionHost(@NotNull Editor editor, @NotNull PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        return findInjectionHost(offset, file);
    }

    @Nullable
    public static PsiLanguageInjectionHost findInjectionHost(final int offset, @NotNull PsiFile file) {
        FileViewProvider viewProvider = file.getViewProvider();
        for (Language language : viewProvider.getLanguages()) {
            PsiLanguageInjectionHost host = PsiTreeUtil.getParentOfType(
                    viewProvider.findElementAt(offset, language),
                    PsiLanguageInjectionHost.class, false);
            if (host != null && host.isValidHost()) return host;
        }
        return null;
    }

    public static @NotNull String getRelativePath(@NotNull Project project, @NotNull PsiFile psiFile) {
        VirtualFile vf = psiFile.getOriginalFile().getVirtualFile();

        if (ApplicationManager.getApplication().isUnitTestMode()) return Path.of(vf.getPath()).toString();

        if (!vf.isInLocalFileSystem()) return String.format("virtual: %s", vf.getPath());

        return Path.of(Objects.requireNonNull(project.getBasePath())).relativize(Path.of(vf.getPath())).toString();
    }
}
