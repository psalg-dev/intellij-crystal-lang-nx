package org.intellij.crystal.ide.project;

import org.intellij.crystal.ide.tool.CrystalLocalToolPeer;

public class CrystalProjectWorkspaceSettings {

    private static final String SERVICE_NAME = "CrystalProjectWorkspaceSettings";

    public void setCurrentState(State workspaceSettings) {

    }

    public CrystalCompiler getCompiler() {
        return new CrystalCompiler(new CrystalLocalToolPeer("/usr/bin/crystal"));
    }

    public static class State {
        public String getCompilerPath() {
            return "/usr/bin/crystal";
        }
    }


}
