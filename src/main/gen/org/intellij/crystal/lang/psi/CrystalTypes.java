// This is a generated file. Not intended for manual editing.
package org.intellij.crystal.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.crystal.lang.psi.impl.*;

public interface CrystalTypes {

  IElementType PROPERTY = new CrystalElementType("PROPERTY");

  IElementType COMMENT = new CrystalTokenType("COMMENT");
  IElementType CRLF = new CrystalTokenType("CRLF");
  IElementType KEY = new CrystalTokenType("KEY");
  IElementType SEPARATOR = new CrystalTokenType("SEPARATOR");
  IElementType VALUE = new CrystalTokenType("VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PROPERTY) {
        return new CrPropertyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
