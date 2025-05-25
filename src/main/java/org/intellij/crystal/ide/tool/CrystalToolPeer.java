package org.intellij.crystal.ide.tool;

import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.GeneralCommandLine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CrystalToolPeer {
    protected final String localPath;

    public static final CrystalToolPeer EMPTY = new CrystalLocalToolPeer("");

    protected CrystalToolPeer(String localPath) {
        this.localPath = localPath;
    }

    public abstract Path getFullPath();

    public boolean isValid() {
        return Files.exists(getFullPath()) && Files.isRegularFile(getFullPath());
    }

    public abstract String convertArgumentPath(String path);

    public GeneralCommandLine buildCommandLine(
            java.util.List<?> parameters,
            EnvironmentVariablesData env
    ) {
        if (!isValid()) return null;
        GeneralCommandLine commandLine = new GeneralCommandLine().withExePath(localPath);
        if (env != null) {
            env.configureCommandLine(commandLine, true);
        } else {
            commandLine.withEnvironment(System.getenv());
        }
        commandLine.withParameters(parameters.stream().map(it -> {
            if (it instanceof File) {
                return convertArgumentPath(((File) it).getAbsolutePath());
            } else {
                return it.toString();
            }
        }).toArray(String[]::new));
        return commandLine;
    }
}

