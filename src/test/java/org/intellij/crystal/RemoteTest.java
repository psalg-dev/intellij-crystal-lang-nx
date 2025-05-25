package org.intellij.crystal;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.dataExtractor.RemoteText;
import com.intellij.remoterobot.launcher.Ide;
import com.intellij.remoterobot.launcher.IdeDownloader;
import com.intellij.remoterobot.launcher.IdeLauncher;
import kotlin.Unit;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;


@ExtendWith(RemoteRobotExtension.class)
public class RemoteTest {
    private static Process ideaProcess;
    private static Path tmpDir;
    private static RemoteRobot remoteRobot;
    private final static Ide.BuildType buildType = Ide.BuildType.RELEASE;
    private final static String version = "2024.2.6";

    static {
        try {
            tmpDir = Files.createTempDirectory("launcher");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @BeforeAll
    public static void before() {
        final OkHttpClient client = new OkHttpClient();
        remoteRobot = new RemoteRobot("http://localhost:8082", client);
        final IdeDownloader ideDownloader = new IdeDownloader(client);
        final Map<String, Object> additionalProperties = new HashMap<>();
        additionalProperties.put("robot-server.port", 8082);

        final List<Path> plugins = new ArrayList<>();
        plugins.add(ideDownloader.downloadRobotPlugin(tmpDir));
        plugins.add(Paths.get("build/libs/intellij-crystal-lang-nx-1.0-NX-SNAPSHOT.jar"));

        // plugins.add(path to your plugin)
        ideaProcess = IdeLauncher.INSTANCE.launchIde(
                ideDownloader.downloadAndExtract(Ide.IDEA_COMMUNITY, tmpDir, buildType, version),
                additionalProperties,
                List.of("-Dide.show.tips.on.startup.default.value=false", "-Didea.trust.all.projects=true"),
                plugins,
                tmpDir,
                map -> {
                    return Unit.INSTANCE;
                }
        );
        waitFor(Duration.ofSeconds(90), Duration.ofSeconds(5), () -> isAvailable(remoteRobot));
    }

    private static boolean isAvailable(RemoteRobot remoteRobot) {
        try {
            remoteRobot.callJs("true");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @AfterAll
    public static void after() throws IOException {
        ideaProcess.destroy();
        FileUtils.cleanDirectory(tmpDir.toFile());
    }

    @Test
    public void testCreateProject_moduleShouldBeSuccessfullyLoaded() {
        RemoteRobot robot = RemoteTest.remoteRobot;

        WelcomeFrame welcomeFrame = robot.find(WelcomeFrame.class, Duration.ofSeconds(5));
        welcomeFrame.getConfirmScriptLauncherHint().click();
        welcomeFrame.getCreateNewProjectLink().click();


        NewProjectFrame newProjectFrame = robot.find(NewProjectFrame.class, Duration.ofSeconds(5));
        RemoteText crystal = newProjectFrame.getProjectWizardList().findText("Crystal");
        crystal.click();

        RemoteText projectName = newProjectFrame.getNewProjectNameField().findAllText().iterator().next();

        newProjectFrame.getCreateProjectButton().click();

        IdeWindowFrame ideWindowFrame = robot.find(IdeWindowFrame.class, Duration.ofSeconds(30));
        ComponentFixture projectViewTree = ideWindowFrame.getProjectViewTree();
        waitFor(() -> !projectViewTree.findAllText("shard.yml").isEmpty());

        List<RemoteText> allText = projectViewTree.findAllText();
        List<String> projectTreeNodes = allText.stream().map(RemoteText::getText).toList();
        assert projectTreeNodes.contains("src");
        assert projectTreeNodes.contains("spec");
        assert projectTreeNodes.contains("shard.yml");

        allText.stream().filter(text -> text.getText().contains("src")).findFirst().ifPresent(RemoteText::doubleClick);
        allText.stream().filter(text -> text.getText().contains("spec")).findFirst().ifPresent(RemoteText::doubleClick);
        allText = projectViewTree.findAllText();
        projectTreeNodes = allText.stream().map(RemoteText::getText).toList();
        assert projectTreeNodes.contains(projectName.getText() + ".cr");
        assert projectTreeNodes.contains(projectName.getText() + "_spec.cr");

    }

}
