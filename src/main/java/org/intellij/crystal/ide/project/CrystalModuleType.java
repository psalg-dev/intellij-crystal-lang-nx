package org.intellij.crystal.ide.project;

import com.intellij.openapi.module.ModuleType;
import org.intellij.crystal.CrystalIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CrystalModuleType extends ModuleType<CrystalModuleBuilder> {

    public static final CrystalModuleType INSTANCE = new CrystalModuleType();

    public CrystalModuleType() {
        super("CRYSTAL_MODULE");
    }

    @Override
    public @NotNull CrystalModuleBuilder createModuleBuilder() {
        return new CrystalModuleBuilder();
    }

    @Override
    public @NotNull String getName() {
        return "Crystal";
    }

    @Override
    public @NotNull String getDescription() {
        return "Crystal module";
    }

    @Override
    public @NotNull Icon getNodeIcon(boolean isOpened) {
        return CrystalIcons.FILE;
    }


}
