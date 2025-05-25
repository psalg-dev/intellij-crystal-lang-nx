package org.intellij.crystal.ide.project;

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.platform.DirectoryProjectGeneratorBase;
import com.intellij.platform.ProjectGeneratorPeer;
import org.intellij.crystal.CrystalIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static org.intellij.crystal.ide.project.CrystalModuleBuilder.generateCrystalProject;

public class CrystalDirectoryProjectGenerator extends DirectoryProjectGeneratorBase<CrystalProjectGeneratorConfig>
        implements CustomStepProjectGenerator<CrystalProjectGeneratorConfig> {

    @Override
    public String getName() {
        return "Crystal";
    }

    @Override
    public Icon getLogo() {
        return CrystalIcons.FILE;
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull CrystalProjectGeneratorConfig settings, com.intellij.openapi.module.@NotNull Module module) {
        generateCrystalProject(project, baseDir, settings);
    }

    @Override
    public CrystalProjectSettingsStep createStep(
            DirectoryProjectGenerator<CrystalProjectGeneratorConfig> projectGenerator,
            AbstractNewProjectStep.AbstractCallback<CrystalProjectGeneratorConfig> callback) {
        return new CrystalProjectSettingsStep(projectGenerator);
    }

    @Override
    public @NotNull NotNullLazyValue<ProjectGeneratorPeer<CrystalProjectGeneratorConfig>> createLazyPeer() {
        return super.createLazyPeer();
    }
}