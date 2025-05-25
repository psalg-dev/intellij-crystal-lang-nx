package org.intellij.crystal.ide.project.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CrystalFileBuildConfiguration extends CrystalFileRunConfigurationBase {
    private String outputFileName = "";

    public CrystalFileBuildConfiguration(Project project, String name, ConfigurationFactory factory) {
        super(project, name, factory);
    }

    @Override
    public boolean getShowProgress() {
        return true;
    }

    @Override
    public String getSuggestedNamePrefix() {
        return "Build";
    }

    @Override
    public String getCommandArgument() {
        return "build";
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        //TODO: implement run editor
        return null;
    }

    @Override
    public void readExternal(Element element) {
        super.readExternal(element);
        String value = getNestedString(element, "outputFileName");
        outputFileName = value != null ? value : "";
    }

    @Override
    public void writeExternal(Element element) {
        super.writeExternal(element);
        addNestedValue(element, "outputFileName", outputFileName);
    }

    @Override
    public void patchArgumentList(ArrayList<Object> arguments) {
        if (outputFileName != null && !outputFileName.isBlank()) {
            arguments.add("-o");
            arguments.add(outputFileName);
        }
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    // Utility methods for XML handling (assumed to be similar to Kotlin extensions)
    private static String getNestedString(Element element, String name) {
        Element child = element.getChild(name);
        return child != null ? child.getText() : null;
    }

    private static void addNestedValue(Element element, String name, String value) {
        Element child = new Element(name);
        child.setText(value);
        element.addContent(child);
    }
}