package org.intellij.crystal;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.DefaultXpath;
import com.intellij.remoterobot.search.locators.Locators;
import org.jetbrains.annotations.NotNull;

@DefaultXpath(by = "New Project Dialog", xpath = "//div[@class='MyDialog']")
public class NewProjectFrame extends CommonContainerFixture {
    public NewProjectFrame(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }

    public ComponentFixture getNewProjectNameField() {
        return textField(
                Locators.byXpath(
                        "New Project Name",
                        "//div[@class='JBTextField']"
                )
        );
    }

    public ComponentFixture getProjectWizardList() {
        return jList(
                Locators.byXpath(
                        "Crystal New Project Wizard",
                        "//div[(@class='JBList' )]"
                )
        );
    }

    public ComponentFixture getCreateProjectButton() {
        return actionLink(
                Locators.byXpath(
                        "Create Crystal Project",
                        "//div[@text='Create']"
                )
        );
    }
}
