package org.intellij.crystal.lang.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.crystal.CrystalLanguage;

public class CrystalElementType extends IElementType {

    public CrystalElementType(String debugName) {
        super(debugName, CrystalLanguage.INSTANCE);
    }

}
