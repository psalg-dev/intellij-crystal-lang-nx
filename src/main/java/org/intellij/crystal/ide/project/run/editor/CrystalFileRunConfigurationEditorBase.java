package org.intellij.crystal.ide.project.run.editor;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CrystalFileRunConfigurationEditorBase<T> extends SettingsEditor<T> {

    @Override
    protected void resetEditorFrom(@NotNull T s) {

    }

    @Override
    protected void applyEditorTo(@NotNull T s) throws ConfigurationException {

    }

    @Override
    protected @NotNull JComponent createEditor() {
        return null;
    }
}
