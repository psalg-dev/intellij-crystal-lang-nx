package org.intellij.crystal.ide.project.tree;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class CrystalProjectStructureProvider implements TreeStructureProvider {

    private boolean addedParent = false;

    @NotNull
    @Override
    public Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent,
                                                  @NotNull Collection<AbstractTreeNode<?>> children,
                                                  ViewSettings settings) {

        boolean hasShardYaml = children.stream().filter(node -> node instanceof PsiFileNode)
                .anyMatch(node -> ((PsiFileNode) node).getVirtualFile().getPath().endsWith("shard.yml"));

        if (hasShardYaml) {
            if (!addedParent) {
                System.out.println("Adding parent...");
                List<AbstractTreeNode<?>> result = new ArrayList<>();
                result.add(parent);
                addedParent = true;
                result.addAll(children);
                return result;
            }
            return children;
        } else {
            return children;
        }
    }

}