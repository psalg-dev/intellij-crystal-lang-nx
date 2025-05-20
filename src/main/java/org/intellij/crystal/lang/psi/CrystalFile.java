package org.intellij.crystal.lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.psi.FileViewProvider;
import org.intellij.crystal.CrystalFileType;
import org.intellij.crystal.CrystalLanguage;
import org.jetbrains.annotations.NotNull;

public class CrystalFile extends PsiFileBase {

    public CrystalFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CrystalLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public CrystalFileType getFileType() {
        return CrystalFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Crystal File";
    }
}
