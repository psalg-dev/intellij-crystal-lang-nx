package org.intellij.crystal.ide.project;


import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.crystal.ProcessUtilsExt;
import org.intellij.crystal.ide.tool.CrystalToolPeer;
import org.jetbrains.annotations.NonNls;

import java.util.Arrays;

public class CrystalCompiler {
    public static final @NonNls String SHARD_YAML_NAME = "shard.yml";
    private final CrystalToolPeer peer;

    public CrystalCompiler(CrystalToolPeer peer) {
        this.peer = peer;
    }

    public ProcessUtilsExt.CrResult<CrystalGeneratedProjectLayout, ProcessUtilsExt.CrProcessExecutionException> generateProject(VirtualFile baseDir, String name, CrystalProjectTemplate template) {
        String templateArg;
        switch (template) {
            case APPLICATION:
                templateArg = "app";
                break;
            case LIBRARY:
                templateArg = "lib";
                break;
            default:
                throw new IllegalArgumentException("Unknown template: " + template);
        }

        String path = baseDir.getParent().getPath();
        GeneralCommandLine cmdLine = peer.buildCommandLine(Arrays.asList("init", templateArg, name, "--force"), null).withWorkDirectory(path);

        ProcessUtilsExt.execute(cmdLine, null, CapturingProcessHandler::runProcess);

        VfsUtil.markDirtyAndRefresh(true, true, true, baseDir);

        VirtualFile shardYaml = baseDir.findChild(SHARD_YAML_NAME);
        VirtualFile mainFile = baseDir.findFileByRelativePath("src/" + name + ".cr");
        return ProcessUtilsExt.CrResult.Ok(new CrystalGeneratedProjectLayout(shardYaml, mainFile));
    }

    public CrystalToolPeer getPeer() {
        return peer;
    }
}