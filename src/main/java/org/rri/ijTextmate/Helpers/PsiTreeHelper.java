package org.rri.ijTextmate.Helpers;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PsiTreeHelper {
    public static <T extends PsiElement> @NotNull List<T> allNeighboringLeavesOfType(PsiElement root, Class<? extends T> clazz) {
        List<T> result = new ArrayList<>();
        if (root == null) return result;
        Set<PsiElement> visited = new HashSet<>();

        if (instanceOf(root, clazz)) result.add(clazz.cast(root));

        visited.add(root);
        root = root.getParent();

        boolean exists = true;
        while (exists) {
            exists = false;
            for (PsiElement element : root.getChildren()) {
                exists |= recursiveTravel(element, visited, result, clazz);
            }
            root = root.getParent();
        }
        return result;
    }

    private static <T> boolean recursiveTravel(PsiElement root, @NotNull Set<PsiElement> visited, List<T> hosts, Class<? extends T> clazz) {
        if (visited.contains(root)) return false;
        visited.add(root);

        if (clazz.isInstance(root)) {
            hosts.add(clazz.cast(root));
            return true;
        }

        boolean result = false;

        for (PsiElement child : root.getChildren()) {
            result |= recursiveTravel(child, visited, hosts, clazz);
        }

        return result;
    }

    @Contract(value = "null, _ -> false", pure = true)
    private static <T extends PsiElement> boolean instanceOf(PsiElement element, @NotNull Class<T> clazz) {
        return clazz.isInstance(element);
    }
}
