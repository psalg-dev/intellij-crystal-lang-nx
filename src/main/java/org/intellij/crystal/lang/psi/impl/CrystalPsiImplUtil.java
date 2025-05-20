package org.intellij.crystal.lang.psi.impl;
// see https://plugins.jetbrains.com/docs/intellij/psi-helper-and-utilities.html#define-helper-methods-for-generated-psi-elements
import com.intellij.lang.ASTNode;
import org.intellij.crystal.lang.psi.CrProperty;
import org.intellij.crystal.lang.psi.CrystalTypes;

public class CrystalPsiImplUtil {

    public static String getKey(CrProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(CrystalTypes.KEY);
        if (keyNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to simple spaces
            return keyNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return null;
        }
    }

    public static String getValue(CrProperty element) {
        ASTNode valueNode = element.getNode().findChildByType(CrystalTypes.VALUE);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }


}
