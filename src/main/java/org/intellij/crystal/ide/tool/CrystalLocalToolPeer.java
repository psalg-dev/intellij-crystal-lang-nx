package org.intellij.crystal.ide.tool;

import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class CrystalLocalToolPeer extends CrystalToolPeer {
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
