package org.intellij.crystal.ide.project.wizard;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.observable.properties.PropertyGraph;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.intellij.crystal.CrystalIcons;
import org.intellij.crystal.ide.project.CrystalProjectGeneratorConfig;
import org.intellij.crystal.ide.project.CrystalProjectSettings;
import org.intellij.crystal.ide.project.CrystalProjectTemplate;
import org.intellij.crystal.ide.project.CrystalProjectWorkspaceSettings;
import org.intellij.crystal.ide.project.module.CrystalModuleBuilder;
import org.intellij.crystal.ide.project.module.CrystalModuleType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Paths;

public class CrystalLanguageGeneratorNewProjectWizard implements LanguageGeneratorNewProjectWizard {
    @Override
    public @NotNull Icon getIcon() {
        return CrystalIcons.FILE;
    }

    @Override
    public @NotNull String getName() {
        return "Crystal";
    }

    @Override
    public @NotNull NewProjectWizardStep createStep(@NotNull NewProjectWizardStep newProjectWizardStep) {
        return new CrystalLanguageNewProjectWizardStep(newProjectWizardStep);
    }

    public static class CrystalLanguageNewProjectWizardStep implements NewProjectWizardStep {

        private final NewProjectWizardStep parent;
        private final CrystalModuleBuilder moduleBuilder = new CrystalModuleBuilder(true);

        public CrystalLanguageNewProjectWizardStep(@NotNull NewProjectWizardStep parent) {
            this.parent = parent;
        }

        @Override
        public @NotNull WizardContext getContext() {
            return parent.getContext();
        }

        @Override
        public @NotNull PropertyGraph getPropertyGraph() {
            return parent.getPropertyGraph();
        }

        @Override
        public @NotNull Keywords getKeywords() {
            return parent.getKeywords();
        }

        @Override
        public @NotNull UserDataHolder getData() {
            return parent.getData();
        }

        /**
         * This code runs when clicking "new" -> "project" -> "Crystal" in the IDE.
         */
        @Override
        public void setupProject(@NotNull Project project) {
            // TODO: this should be supplied by user..
            CrystalProjectGeneratorConfig config = new CrystalProjectGeneratorConfig(CrystalProjectTemplate.APPLICATION, new CrystalProjectSettings.State(), new CrystalProjectWorkspaceSettings.State());

            var root = VirtualFileManager.getInstance().findFileByNioPath(Paths.get(project.getBasePath()));

            // this generates the files on disk
            CrystalModuleBuilder.generateCrystalProject(project, root, config);

            // this sets up an intellij module for the project so it is rendered correctly in the
            // project view of intellij
            ModuleManager moduleManager = ModuleManager.getInstance(project);
            ApplicationManager.getApplication().runWriteAction(() -> {
                Module newModule = moduleManager.newModule(Paths.get(project.getBasePath()), CrystalModuleType.INSTANCE.getId());
                ModifiableRootModel modifiableRootModel = ModuleRootManager.getInstance(newModule).getModifiableModel();
                moduleBuilder.setupRootModel(modifiableRootModel);
            });
        }
    }

}
