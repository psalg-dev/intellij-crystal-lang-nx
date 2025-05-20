package org.intellij.crystal.ide.project;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.openapi.observable.properties.PropertyGraph;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolder;
import org.jetbrains.annotations.NotNull;


public class CrystalProjectGeneratorPanel implements NewProjectWizardStep {

    private final String store;
    private final NewProjectWizardStep parent;

    public CrystalProjectGeneratorPanel(String store, NewProjectWizardStep parent) {
        this.store = store;
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

    @Override
    public void setupProject(@NotNull Project project) {
        NewProjectWizardStep.super.setupProject(project);
    }
}
