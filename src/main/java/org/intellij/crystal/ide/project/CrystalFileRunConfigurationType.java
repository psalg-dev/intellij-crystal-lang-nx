package org.intellij.crystal.ide.project;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import org.intellij.crystal.CrystalIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CrystalFileRunConfigurationType extends ConfigurationTypeBase {
    public CrystalFileRunConfigurationType() {
        super("CRYSTAL_RUN_CONFIGURATION",
                "Crystal",
                "Crystal run configuration",
                NotNullLazyValue.createValue(() -> CrystalIcons.FILE));
        addFactory(
                new ConfigurationFactory(this) {

                    @Override
                    public @NotNull String getId() {
                        return "Crystal Build";
                    }

                    @Override
                    public @Nullable Icon getIcon() {
                        return CrystalIcons.FILE;
                    }

                    @Override
                    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                        return new CrystalFileBuildConfiguration(project, "Crystal Build", this);
                    }
                }
        );
    }

    public static CrystalFileRunConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(CrystalFileRunConfigurationType.class);
    }

}
