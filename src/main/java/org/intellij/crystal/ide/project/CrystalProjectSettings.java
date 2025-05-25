package org.intellij.crystal.ide.project;

import java.util.LinkedHashSet;

public class CrystalProjectSettings {

    public void setCurrentState(State settings) {

    }

    public static class State {
        //     @Attribute(value = "languageLevel", converter = VersionConverter.class)
//     private CrystalVersion languageVersion = DEFAULT_LANGUAGE_VERSION;
        private String mainFilePath = "";
        private boolean useFormatTool = false;
        private boolean runFormatToolOnSave = false;
        private final LinkedHashSet<String> flags = new LinkedHashSet<>();

        public State() {
        }

//     public CrystalVersion getLanguageVersion() {
//         return languageVersion;
//     }
//
//     public void setLanguageVersion(CrystalVersion languageVersion) {
//         this.languageVersion = languageVersion;
//     }

        public String getMainFilePath() {
            return mainFilePath;
        }

        public void setMainFilePath(String mainFilePath) {
            this.mainFilePath = mainFilePath;
        }

        public boolean isUseFormatTool() {
            return useFormatTool;
        }

        public void setUseFormatTool(boolean useFormatTool) {
            this.useFormatTool = useFormatTool;
        }

        public boolean isRunFormatToolOnSave() {
            return runFormatToolOnSave;
        }

        public void setRunFormatToolOnSave(boolean runFormatToolOnSave) {
            this.runFormatToolOnSave = runFormatToolOnSave;
        }

        public LinkedHashSet<String> getFlags() {
            return flags;
        }
    }


}
