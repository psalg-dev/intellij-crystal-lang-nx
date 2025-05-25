package org.intellij.crystal;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.DefaultXpath;
import com.intellij.remoterobot.search.locators.Locators;

import java.time.Duration;
import java.util.function.Consumer;

@DefaultXpath(by = "Welcome Frame", xpath = "//div[@class='FlatWelcomeFrame']")
public class WelcomeFrame extends CommonContainerFixture {
    public WelcomeFrame(RemoteRobot remoteRobot, RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }

    public ComponentFixture getConfirmScriptLauncherHint() {
        return actionLink(
                Locators.byXpath(
                        "Don't show again",
                        "//div[@text.key='sys.health.acknowledge.action']"
                )
        );
    }

    public ComponentFixture getCreateNewProjectLink() {
        return actionLink(
                Locators.byXpath(
                        "New Project",
                        "//div[(@class='MainButton' and @text='New Project') or (@accessiblename='New Project' and @class='JButton')]"
                )
        );
    }

    public ComponentFixture getMoreActions() {
        return button(
                Locators.byXpath("More Action", "//div[@accessiblename='More Actions']")
        );
    }

    public ComponentFixture getHeavyWeightPopup() {
        return getRemoteRobot().find(
                ComponentFixture.class,
                Locators.byXpath("//div[@class='HeavyWeightWindow']")
        );
    }

    public static void welcomeFrame(RemoteRobot remoteRobot, Consumer<WelcomeFrame> function) {
        WelcomeFrame frame = remoteRobot.find(WelcomeFrame.class, Duration.ofSeconds(10));
        function.accept(frame);
    }
}