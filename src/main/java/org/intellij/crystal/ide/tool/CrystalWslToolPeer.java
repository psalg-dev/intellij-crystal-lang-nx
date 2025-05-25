package org.intellij.crystal.ide.tool;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.wsl.WSLCommandLineOptions;
import com.intellij.execution.wsl.WSLDistribution;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class CrystalWslToolPeer extends CrystalToolPeer {
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