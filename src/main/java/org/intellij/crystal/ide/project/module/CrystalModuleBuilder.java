package org.intellij.crystal.ide.project.module;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import org.intellij.crystal.ProcessUtilsExt;
import org.intellij.crystal.ide.project.*;
import org.intellij.crystal.ide.project.run.CrystalFileRunConfigurationType;
import org.jetbrains.annotations.NotNull;

import static java.util.Optional.ofNullable;

public class CrystalModuleBuilder extends ModuleBuilder {

    private CrystalProjectGeneratorConfig config;
    private boolean shouldSkipGenerate = false;

    public CrystalModuleBuilder() {
        super();
        config = new CrystalProjectGeneratorConfig(
                CrystalProjectTemplate.APPLICATION,
                new CrystalProjectSettings.State(),
                new CrystalProjectWorkspaceSettings.State()
        );
    }

    public CrystalModuleBuilder(boolean shouldSkipGenerate) {
        this();
        this.shouldSkipGenerate = shouldSkipGenerate;
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) {
        modifiableRootModel.inheritSdk();

        String basePath = modifiableRootModel.getProject().getBasePath();

        ContentEntry contentEntry = ofNullable(doAddContentEntry(modifiableRootModel)).orElseGet(() -> {
            setContentEntryPath(basePath);
            return doAddContentEntry(modifiableRootModel);
        });

        VirtualFile root = ofNullable(contentEntry.getFile()).map(r -> {
            ofNullable(r.findChild("src")).ifPresent(srcDir -> contentEntry.addSourceFolder(srcDir, false));
            ofNullable(r.findChild("spec")).ifPresent(srcDir -> contentEntry.addSourceFolder(srcDir, true));
            modifiableRootModel.commit();
            r.refresh(false, true);
            return r;
        }).orElse(null);

        if (config == null) {
            config = new CrystalProjectGeneratorConfig(
                    CrystalProjectTemplate.APPLICATION,
                    new CrystalProjectSettings.State(),
                    new CrystalProjectWorkspaceSettings.State()
            );
        }
        if (!shouldSkipGenerate) {
            if (root != null) {
                generateCrystalProject(modifiableRootModel.getProject(), root, config);
            } else {
                System.err.println("Root directory is null, cannot generate Crystal project.");
            }
        }
    }

    @Override
    public ModuleType<?> getModuleType() {
        return CrystalModuleType.INSTANCE;
    }


    public static void generateCrystalProject(Project project, VirtualFile baseDir, CrystalProjectGeneratorConfig config) {
        // Implement the logic to generate a Crystal project
        // This is a placeholder for the actual implementation

        String compilerPath = config.getWorkspaceSettings().getCompilerPath();
        CrystalCompiler crystalCompiler = new CrystalCompiler(getCrystalTool(compilerPath));

        ProcessUtilsExt.CrResult<CrystalGeneratedProjectLayout, ProcessUtilsExt.CrProcessExecutionException> layoutResult = ProgressManager
                .getInstance()
                .runProcessWithProgressSynchronously(
                        () -> crystalCompiler.generateProject(baseDir, baseDir.getName(), config.getTemplate()),
                        "Generating Crystal Project...",
                        true,
                        project
                );
        if (layoutResult.isErr()) {
            CommonRefactoringUtil.showErrorHint(
                    project,
                    null,
                    "Error generating project: " + layoutResult.getErr().getMessage(),
                    "Project Generation Error",
                    null
            );
        }
        CrystalGeneratedProjectLayout layout = layoutResult.getOk();
        if (layout != null) {
            config.getSettings().setMainFilePath(layout.mainFile().getPath());

            CrystalProjectSettings projectSettings = project.getService(CrystalProjectSettings.class);
            projectSettings.setCurrentState(config.getSettings());

            CrystalProjectWorkspaceSettings workspaceSettings = project.getService(CrystalProjectWorkspaceSettings.class);
            workspaceSettings.setCurrentState(config.getWorkspaceSettings());

            addDefaultRunConfiguration(project, config.getTemplate(), layout);

        }

    }

    static void addDefaultRunConfiguration(Project project, CrystalProjectTemplate template, CrystalGeneratedProjectLayout layout) {
        if (template != CrystalProjectTemplate.APPLICATION) return;
        RunManager runManager = RunManager.getInstance(project);

        RunnerAndConfigurationSettings buildConfig = runManager
                .createConfiguration("Build", CrystalFileRunConfigurationType.class);
        RunnerAndConfigurationSettings runConfig = runManager
                .createConfiguration("Run", CrystalFileRunConfigurationType.class);

//        ((CrystalFileBuildConfigurationType) buildConfig.getConfiguration())
//        ((CrystalFileBuildConfiguration) buildConfig.getConfiguration())
//                .setFileAndWorkingDirectory(layout.getMainFile(), layout.getShardYaml());
        buildConfig.setActivateToolWindowBeforeRun(false);
        runManager.addConfiguration(buildConfig);


//        ((CrystalFileBuildConfigurationType) runConfig.getConfiguration())
//                .setFileAndWorkingDirectory(layout.getMainFile(), layout.getShardYaml());
        runManager.addConfiguration(runConfig);

        runManager.setSelectedConfiguration(runConfig);
    }

    public static CrystalToolPeer getCrystalTool(String compilerPath) {
        return new CrystalLocalToolPeer(compilerPath);
    }
}
