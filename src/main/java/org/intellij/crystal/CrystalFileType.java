package org.intellij.crystal;

import com.intellij.openapi.fileTypes.LanguageFileType;

import javax.swing.*;

public class CrystalFileType extends LanguageFileType {

    public static final CrystalFileType INSTANCE = new CrystalFileType();

    private CrystalFileType() {
        super(CrystalLanguage.INSTANCE);
    }

    @Override
    public String getName() {
        return "Crystal File";
    }

    @Override
    public String getDescription() {
        return "Crystal programming language file";
    }

    @Override
    public String getDefaultExtension() {
        return "cr";
    }

    @Override
    public Icon getIcon() {
        return CrystalIcons.FILE;
    }

}
