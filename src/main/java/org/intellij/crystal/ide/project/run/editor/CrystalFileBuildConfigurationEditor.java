package org.intellij.crystal.ide.project.run.editor;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBTextField;
import org.intellij.crystal.ide.project.run.CrystalFileBuildConfiguration;
import org.jetbrains.annotations.NotNull;

public class CrystalFileBuildConfigurationEditor extends CrystalFileRunConfigurationEditorBase<CrystalFileBuildConfiguration> {

    private JBTextField outputFileNameEditor;



    @Override
    protected void resetEditorFrom(@NotNull CrystalFileBuildConfiguration s) {
        super.resetEditorFrom(s);
        outputFileNameEditor.setText(s.getOutputFileName());
    }

    @Override
    protected void applyEditorTo(@NotNull CrystalFileBuildConfiguration s) throws ConfigurationException {
        super.applyEditorTo(s);
        s.setOutputFileName(outputFileNameEditor.getText());
    }

}
