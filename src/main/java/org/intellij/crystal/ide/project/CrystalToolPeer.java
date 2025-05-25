package org.intellij.crystal.ide.project;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.wsl.WSLCommandLineOptions;
import com.intellij.execution.wsl.WSLDistribution;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

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

class CrystalLocalToolPeer extends CrystalToolPeer {
    private final Path fullPath;

    public CrystalLocalToolPeer(String path) {
        super(path);
        this.fullPath = toPathOrEmpty(localPath);
    }

    private Path toPathOrEmpty(@NotNull @NlsSafe String windowsPath) {
        try {
            return Path.of(windowsPath);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Path getFullPath() {
        return fullPath;
    }

    @Override
    public String convertArgumentPath(String path) {
        return PathUtil.toSystemDependentName(path);
    }
}

class CrystalWslToolPeer extends CrystalToolPeer {
    private final WSLDistribution distribution;
    private final Path fullPath;

    public CrystalWslToolPeer(String localPath, WSLDistribution distribution) {
        super(localPath);
        this.distribution = distribution;
        this.fullPath = toPathOrEmpty(distribution.getWindowsPath(localPath));
    }

    private Path toPathOrEmpty(@NotNull @NlsSafe String windowsPath) {
        try {
            return Path.of(windowsPath);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Path getFullPath() {
        return fullPath;
    }

    @Override
    public String convertArgumentPath(String path) {
        String wslPath = distribution.getWslPath(path);
        return wslPath != null ? wslPath : "";
    }

    @Override
    public GeneralCommandLine buildCommandLine(
            java.util.List<?> parameters,
            EnvironmentVariablesData env
    ) {
        GeneralCommandLine base = super.buildCommandLine(parameters, env);
        if (base == null) return null;
        try {
            return distribution.patchCommandLine(
                    base,
                    null,
                    new WSLCommandLineOptions().setExecuteCommandInShell(false)
            );
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}