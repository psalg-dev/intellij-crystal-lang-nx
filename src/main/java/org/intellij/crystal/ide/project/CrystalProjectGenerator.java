package org.intellij.crystal.ide.project;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard;
import org.intellij.crystal.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CrystalProjectGenerator implements LanguageGeneratorNewProjectWizard {


    @Override
    public @NotNull Icon getIcon() {
        return Icons.FILE;
    }

    @Override
    public @NotNull String getName() {
        return "Crystal";
    }

    @Override
    public boolean isEnabled(@NotNull WizardContext context) {
        return true;
    }

    @Override
    public @NotNull NewProjectWizardStep createStep(@NotNull NewProjectWizardStep newProjectWizardStep) {
        return newProjectWizardStep;
    }
}