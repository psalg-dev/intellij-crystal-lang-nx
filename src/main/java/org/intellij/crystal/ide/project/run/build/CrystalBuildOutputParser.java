package org.intellij.crystal.ide.project.run.build;

import com.intellij.build.output.BuildOutputParser;
import com.intellij.openapi.vfs.VfsUtil;

public class CrystalBuildOutputParser implements BuildOutputParser {

    private static final java.util.regex.Pattern COMPILER_PASS_PATTERN = java.util.regex.Pattern.compile("\\[(\\d+)/(\\d+)](.*)");
    private static final java.util.regex.Pattern ERROR_PATTERN = java.util.regex.Pattern.compile("Error:(.*)");
    private static final java.util.regex.Pattern WARNING_PATTERN = java.util.regex.Pattern.compile("Warning:(.*)");
    private static final java.util.regex.Pattern LOCATION_PATTERN = java.util.regex.Pattern.compile("In ([^:]*):(\\d+):(\\d+)");


    private static class Location {
        final String path;
        final int line;
        final int column;

        Location(String path, int line, int column) {
            this.path = path;
            this.line = line;
            this.column = column;
        }
    }

    private final com.intellij.build.progress.BuildProgress<com.intellij.build.progress.BuildProgressDescriptor> buildProgress;
    private final java.nio.file.Path workDirectoryPath;
    public final com.intellij.openapi.project.Project project;
    private Location location = null;
    private final com.intellij.openapi.vfs.VirtualFile rootDir;

    public CrystalBuildOutputParser(
            com.intellij.build.progress.BuildProgress<com.intellij.build.progress.BuildProgressDescriptor> buildProgress,
            java.nio.file.Path workDirectoryPath,
            com.intellij.openapi.project.Project project
    ) {
        this.buildProgress = buildProgress;
        this.workDirectoryPath = workDirectoryPath;
        this.project = project;
        this.rootDir = VfsUtil.findFile(workDirectoryPath, false);
    }


    @Override
    public boolean parse(
            String line,
            com.intellij.build.output.BuildOutputInstantReader reader,
            java.util.function.Consumer<? super com.intellij.build.events.BuildEvent> messageConsumer
    ) {
        java.util.regex.Matcher matcher = COMPILER_PASS_PATTERN.matcher(line);
        if (matcher.matches()) {
            long passNum = Long.parseLong(matcher.group(1));
            long passCount = Long.parseLong(matcher.group(2));
            String message = matcher.group(3).trim();
            buildProgress.progress(message, passCount, passNum, "passes");
            return true;
        }
        matcher = LOCATION_PATTERN.matcher(line);
        if (matcher.matches()) {
            String filePath = matcher.group(1);
            int lineNum = Integer.parseInt(matcher.group(2));
            int columnNum = Integer.parseInt(matcher.group(3));
            location = new Location(filePath, lineNum, columnNum);
            return true;
        }
        matcher = ERROR_PATTERN.matcher(line);
        if (matcher.matches()) {
            reportMessage(matcher, com.intellij.build.events.MessageEvent.Kind.ERROR);
            return true;
        }
        matcher = WARNING_PATTERN.matcher(line);
        if (matcher.matches()) {
            reportMessage(matcher, com.intellij.build.events.MessageEvent.Kind.WARNING);
            return true;
        }
        return true;
    }

    private void reportMessage(java.util.regex.Matcher matcher, com.intellij.build.events.MessageEvent.Kind kind) {
        String message = com.intellij.openapi.util.text.StringUtil.capitalize(matcher.group(1).trim());
        com.intellij.pom.Navigatable navigatable = createNavigatableAndResetLocation();
        buildProgress.message(message, message, kind, navigatable);
    }

    private com.intellij.pom.Navigatable createNavigatableAndResetLocation() {
        com.intellij.pom.Navigatable result = null;
        if (location != null) {
            com.intellij.openapi.vfs.VirtualFile file = rootDir != null
                    ? rootDir.findFileByRelativePath(location.path)
                    : null;
            if (file != null) {
                result = new com.intellij.openapi.fileEditor.OpenFileDescriptor(
                        project, file, location.line - 1, location.column - 1
                );
            }
            location = null;
        }
        return result;
    }

}
