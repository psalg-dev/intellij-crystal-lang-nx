package org.intellij.crystal.lang.psi;
// see https://plugins.jetbrains.com/docs/intellij/psi-helper-and-utilities.html#define-a-utility-to-search-properties

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.crystal.CrystalFileType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CrystalPsiUtil {
    /**
     * Searches the entire project for Simple language files with instances of the Simple property with the given key.
     *
     * @param project current project
     * @param key     to check
     * @return matching properties
     */
    public static List<CrProperty> findProperties(Project project, String key) {
        List<CrProperty> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(CrystalFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            CrystalFile CrystalFile = (CrystalFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (CrystalFile != null) {
                CrProperty[] properties = PsiTreeUtil.getChildrenOfType(CrystalFile, CrProperty.class);
                if (properties != null) {
                    for (CrProperty property : properties) {
                        if (key.equals(property.getKey())) {
                            result.add(property);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<CrProperty> findProperties(Project project) {
        List<CrProperty> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(CrystalFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            CrystalFile CrystalFile = (CrystalFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (CrystalFile != null) {
                CrProperty[] properties = PsiTreeUtil.getChildrenOfType(CrystalFile, CrProperty.class);
                if (properties != null) {
                    Collections.addAll(result, properties);
                }
            }
        }
        return result;
    }

    /**
     * Attempts to collect any comment elements above the Simple key/value pair.
     */
    public static @NotNull String findDocumentationComment(CrProperty property) {
        List<String> result = new LinkedList<>();
        PsiElement element = property.getPrevSibling();
        while (element instanceof PsiComment || element instanceof PsiWhiteSpace) {
            if (element instanceof PsiComment) {
                String commentText = element.getText().replaceFirst("[!# ]+", "");
                result.add(commentText);
            }
            element = element.getPrevSibling();
        }
        return StringUtil.join(Lists.reverse(result), "\n ");
    }
    
}
