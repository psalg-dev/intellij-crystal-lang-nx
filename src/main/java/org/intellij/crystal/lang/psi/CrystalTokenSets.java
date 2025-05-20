package org.intellij.crystal.lang.psi;

import com.intellij.psi.tree.TokenSet;

public interface CrystalTokenSets {

    TokenSet IDENTIFIERS = TokenSet.create(CrystalTypes.KEY);

    TokenSet COMMENTS = TokenSet.create(CrystalTypes.COMMENT);

}
