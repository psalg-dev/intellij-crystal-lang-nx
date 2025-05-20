package org.intellij.crystal.lang.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.crystal.CrystalLanguage;

public class CrystalTokenType extends IElementType {

    public CrystalTokenType(String debugName) {
        super(debugName, CrystalLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "CrystalTokenType." + super.toString();
    }
}
