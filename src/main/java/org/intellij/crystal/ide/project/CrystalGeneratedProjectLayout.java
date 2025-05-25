package org.intellij.crystal.ide.project;

import com.intellij.openapi.vfs.VirtualFile;

public record CrystalGeneratedProjectLayout(VirtualFile shardYaml, VirtualFile mainFile) {
}
