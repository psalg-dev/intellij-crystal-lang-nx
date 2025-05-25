package org.intellij.crystal.ide.project;

import com.intellij.build.BuildContentManager;
import com.intellij.build.BuildViewManager;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import java.nio.file.Path;


public class CrystalFileRunState extends CommandLineState {
    private final GeneralCommandLine commandLine;

    public CrystalFileRunState(GeneralCommandLine commandLine, ExecutionEnvironment environment) {
        super(environment);
        this.commandLine = commandLine;
    }

    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        ColoredProcessHandler handler = new ColoredProcessHandler(commandLine);
        handler.setShouldDestroyProcessRecursively(true);
        ProcessTerminatedListener.attach(handler);
        Project project = getEnvironment().getProject();
        BuildViewManager buildViewManager = project.getService(BuildViewManager.class);
        if (!isHeadlessEnvironment()) {
            ToolWindow buildToolWindow = BuildContentManager.getInstance(project).getOrCreateToolWindow();
            buildToolWindow.setAvailable(true, null);
            if (getEnvironment().getRunnerAndConfigurationSettings().isActivateToolWindowBeforeRun()) {
                buildToolWindow.activate(null);
            }
        }
        Path workDirectoryPath = commandLine.getWorkDirectory().toPath();
        handler.addProcessListener(new CrystalBuildProcessListener(handler, workDirectoryPath, getEnvironment(), buildViewManager));
        handler.startNotify();
        return handler;
    }

    private boolean isHeadlessEnvironment() {
        Application app = ApplicationManager.getApplication();
        return app.isHeadlessEnvironment();
    }
}