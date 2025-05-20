package org.intellij.crystal;

import com.intellij.lang.Language;

public class CrystalLanguage extends Language {
    public static final CrystalLanguage INSTANCE = new CrystalLanguage();

    private CrystalLanguage() {
        super("Crystal");
    }

    @Override
    public String getDisplayName() {
        return "Crystal";
    }

}
