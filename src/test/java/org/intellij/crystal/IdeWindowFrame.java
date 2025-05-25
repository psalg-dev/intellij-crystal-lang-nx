package org.intellij.crystal;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.DefaultXpath;
import com.intellij.remoterobot.search.locators.Locators;
import org.jetbrains.annotations.NotNull;

@DefaultXpath(by = "IdeFrameImpl type", xpath = "//div[@class='IdeFrameImpl']")
public class IdeWindowFrame extends CommonContainerFixture {
    public IdeWindowFrame(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }

    public ComponentFixture getProjectViewTree() {
        return jTree(
                Locators.byXpath(
                        "Project View Tree",
                        "//div[@class='ProjectViewTree']"
                )
        );
    }

}
