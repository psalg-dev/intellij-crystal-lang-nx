package org.intellij.crystal.ide.project.run.build;

import com.intellij.build.BuildContentDescriptor;
import com.intellij.build.BuildViewManager;
import com.intellij.build.DefaultBuildDescriptor;
import com.intellij.build.output.BuildOutputInstantReaderImpl;
import com.intellij.build.progress.BuildProgress;
import com.intellij.build.progress.BuildProgressDescriptor;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.vfs.VfsUtil;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class CrystalBuildProcessListener extends ProcessAdapter {
    private final Path workDirectoryPath;
    private final ExecutionEnvironment environment;
    private final BuildViewManager buildViewManager;
    private final Object buildProgress;
    private final Object buildId;
    private final BuildOutputInstantReaderImpl instantReader;

    private ExecutionListener executionListener;

    public CrystalBuildProcessListener(
            ProcessHandler processHandler,
            Path workDirectoryPath,
            ExecutionEnvironment environment,
            BuildViewManager buildViewManager
    ) {
        this.workDirectoryPath = workDirectoryPath;
        this.environment = environment;
        this.buildViewManager = buildViewManager;

        this.buildProgress = BuildViewManager.createBuildProgress(environment.getProject());
        this.buildId = getBuildProgressId();

        executionListener = environment.getProject().getMessageBus().syncPublisher(ExecutionManager.EXECUTION_TOPIC);
        executionListener.processStarted(String.valueOf(this.hashCode()), environment, processHandler);

        BuildContentDescriptor buildContentDescriptor = new BuildContentDescriptor(null, null, new JComponent() {
        }, "Build");
        DefaultBuildDescriptor descriptor = new DefaultBuildDescriptor(
                new Object(),
                "Crystal Build",
                workDirectoryPath.toString(),
                System.currentTimeMillis()
        ).withContentDescriptor(() -> buildContentDescriptor);

        ((BuildProgress<BuildProgressDescriptor>) buildProgress).start(new BuildProgressDescriptor() {
            @Override
            public String getTitle() {
                return "Build Running...";
            }

            @Override
            public DefaultBuildDescriptor getBuildDescriptor() {
                return descriptor;
            }
        });

        this.instantReader = new BuildOutputInstantReaderImpl(
                buildId,
                buildId,
                buildViewManager,
                java.util.Collections.singletonList(
                        new CrystalBuildOutputParser(
                                (BuildProgress<BuildProgressDescriptor>) buildProgress,
                                workDirectoryPath,
                                environment.getProject()
                        )
                )
        );
    }

    private Object getBuildProgressId() {
        return ((BuildProgress<?>) buildProgress).getId();
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
        executionListener.processTerminating(String.valueOf(this.hashCode()), environment, event.getProcessHandler());
    }

    @Override
    public void processTerminated(ProcessEvent event) {
        instantReader.closeAndGetFuture().whenComplete((result, throwable) -> {
            boolean isSuccess = event.getExitCode() == 0;
            long endTime = System.currentTimeMillis();
            if (isSuccess) {
                ((BuildProgress<?>) buildProgress).finish(endTime, false, "Build successful");
            } else {
                ((BuildProgress<?>) buildProgress).fail(endTime, "Build failed");
            }
            executionListener.processTerminated(String.valueOf(this.hashCode()), environment, event.getProcessHandler(), event.getExitCode());
            Optional.ofNullable(VfsUtil.findFile(workDirectoryPath, true)).ifPresent(
                    virtualFile -> VfsUtil.markDirtyAndRefresh(true, true, true, workDirectoryPath.toFile())
            );

        });
    }
}