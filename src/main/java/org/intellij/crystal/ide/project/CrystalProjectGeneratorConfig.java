package org.intellij.crystal.ide.project;

public class CrystalProjectGeneratorConfig {
    private final CrystalProjectTemplate template;
    private final CrystalProjectSettings.State settings;
    private final CrystalProjectWorkspaceSettings.State workspaceSettings;

    public CrystalProjectGeneratorConfig(
            CrystalProjectTemplate template,
            CrystalProjectSettings.State settings,
            CrystalProjectWorkspaceSettings.State workspaceSettings
    ) {
        this.template = template;
        this.settings = settings;
        this.workspaceSettings = workspaceSettings;
    }

    public CrystalProjectTemplate getTemplate() {
        return template;
    }

    public CrystalProjectSettings.State getSettings() {
        return settings;
    }

    public CrystalProjectWorkspaceSettings.State getWorkspaceSettings() {
        return workspaceSettings;
    }
}