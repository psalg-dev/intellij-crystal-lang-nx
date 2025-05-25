package org.intellij.crystal;


import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.ContainerFixture;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;

public class RemoteRobotExtension implements AfterTestExecutionCallback, ParameterResolver {
    private final String url = System.getProperty("remote-robot-url") != null
            ? System.getProperty("remote-robot-url")
            : "http://127.0.0.1:8082";
    private final RemoteRobot remoteRobot;
    private final OkHttpClient client = new OkHttpClient();

    public RemoteRobotExtension() {
        if ("enable".equals(System.getProperty("debug-retrofit"))) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient debugClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
            remoteRobot = new RemoteRobot(url, debugClient);
        } else {
            remoteRobot = new RemoteRobot(url);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext != null
                && parameterContext.getParameter().getType().equals(RemoteRobot.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return remoteRobot;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Method testMethod = context != null ? context.getRequiredTestMethod() : null;
        if (testMethod == null) {
            throw new IllegalStateException("test method is null");
        }
        String testMethodName = testMethod.getName();
        boolean testFailed = context.getExecutionException().isPresent();
        if (testFailed) {
            // saveScreenshot(testMethodName);
            saveIdeaFrames(testMethodName);
            saveHierarchy(testMethodName);
        }
    }

    private void saveScreenshot(String testName) throws IOException {
        BufferedImage image = fetchScreenShot();
        saveImage(image, testName);
    }

    private void saveHierarchy(String testName) throws IOException {
        File hierarchySnapshot = saveFile(url, "build/reports", "hierarchy-" + testName + ".html");
        File styles = new File("build/reports/styles.css");
        if (!styles.exists()) {
            saveFile(url + "/styles.css", "build/reports", "styles.css");
        }
        System.out.println("Hierarchy snapshot: " + hierarchySnapshot.getAbsolutePath());
    }

    private File saveFile(String url, String folder, String name) throws IOException {
        okhttp3.Response response = client.newCall(new Request.Builder().url(url).build()).execute();
        File dir = new File(folder);
        dir.mkdirs();
        File file = new File(dir, name);
        String body = response.body() != null ? response.body().string() : "";
        FileUtils.writeText(file, body);
        return file;
    }

    private void saveImage(BufferedImage image, String name) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ImageIO.write(image, "png", b);
        byte[] bytes = b.toByteArray();
        File dir = new File("build/reports");
        dir.mkdirs();
        File file = new File(dir, name + ".png");
        FileUtils.writeBytes(file, bytes);
    }

    private void saveIdeaFrames(String testName) {
        for (int n = 0; n < remoteRobot.findAll(ContainerFixture.class, byXpath("//div[@class='IdeFrameImpl']")).size(); n++) {
            ContainerFixture frame = remoteRobot.findAll(ContainerFixture.class, byXpath("//div[@class='IdeFrameImpl']")).get(n);
            try {
                byte[] pic = frame.callJs(
                        "importPackage(java.io);\n" +
                                "importPackage(javax.imageio);\n" +
                                "importPackage(java.awt.image);\n" +
                                "const screenShot = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);\n" +
                                "component.paint(screenShot.getGraphics());\n" +
                                "let pictureBytes;\n" +
                                "const baos = new ByteArrayOutputStream();\n" +
                                "try {\n" +
                                "    ImageIO.write(screenShot, \"png\", baos);\n" +
                                "    pictureBytes = baos.toByteArray();\n" +
                                "} finally {\n" +
                                "  baos.close();\n" +
                                "}\n" +
                                "pictureBytes;", true
                );
                BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(pic));
                saveImage(image, testName + "_" + n);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private BufferedImage fetchScreenShot() throws IOException {
        byte[] pic = remoteRobot.callJs(
                "importPackage(java.io);\n" +
                        "importPackage(javax.imageio);\n" +
                        "const screenShot = new java.awt.Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));\n" +
                        "let pictureBytes;\n" +
                        "const baos = new ByteArrayOutputStream();\n" +
                        "try {\n" +
                        "    ImageIO.write(screenShot, \"png\", baos);\n" +
                        "    pictureBytes = baos.toByteArray();\n" +
                        "} finally {\n" +
                        "  baos.close();\n" +
                        "}\n" +
                        "pictureBytes;"
        );
        return ImageIO.read(new java.io.ByteArrayInputStream(pic));
    }
}

// Utility methods for File (Java 11+)
class FileUtils {
    public static void writeText(File file, String text) throws IOException {
        java.nio.file.Files.writeString(file.toPath(), text);
    }

    public static void writeBytes(File file, byte[] bytes) throws IOException {
        java.nio.file.Files.write(file.toPath(), bytes);
    }
}