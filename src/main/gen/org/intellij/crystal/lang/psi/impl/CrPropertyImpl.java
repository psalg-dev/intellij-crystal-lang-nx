// This is a generated file. Not intended for manual editing.
package org.intellij.crystal.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.crystal.lang.psi.CrystalTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.intellij.crystal.lang.psi.*;

public class CrPropertyImpl extends ASTWrapperPsiElement implements CrProperty {

  public CrPropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CrVisitor visitor) {
    visitor.visitProperty(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CrVisitor) accept((CrVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public String getKey() {
    return CrystalPsiImplUtil.getKey(this);
  }

  @Override
  public String getValue() {
    return CrystalPsiImplUtil.getValue(this);
  }

}
