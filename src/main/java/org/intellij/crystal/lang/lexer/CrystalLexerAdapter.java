package org.intellij.crystal.lang.lexer;

import com.intellij.lexer.FlexAdapter;

public class CrystalLexerAdapter extends FlexAdapter {
    public CrystalLexerAdapter() {
        super(new CrystalLexer(null));
    }

}
