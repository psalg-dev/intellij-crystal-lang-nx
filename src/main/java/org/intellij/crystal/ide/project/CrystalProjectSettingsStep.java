package org.intellij.crystal.ide.project;

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase;
import com.intellij.platform.DirectoryProjectGenerator;

public class CrystalProjectSettingsStep extends ProjectSettingsStepBase<CrystalProjectGeneratorConfig> {
    public CrystalProjectSettingsStep(DirectoryProjectGenerator<CrystalProjectGeneratorConfig> generator) {
        super(generator, new AbstractNewProjectStep.AbstractCallback<>());
    }
}