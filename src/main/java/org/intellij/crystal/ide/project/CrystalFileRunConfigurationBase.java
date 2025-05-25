package org.intellij.crystal.ide.project;

import com.intellij.execution.Executor;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.ProgramParametersConfigurator;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.intellij.crystal.JdomUtils;
import org.jdom.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.intellij.openapi.application.ActionsKt.runReadAction;

abstract class CrystalFileRunConfigurationBase extends LocatableConfigurationBase<RunProfileState> {
    protected Path workingDirectory;
    protected Path targetFile;
    protected EnvironmentVariablesData env = EnvironmentVariablesData.DEFAULT;
    protected String compilerArguments = "";

    public CrystalFileRunConfigurationBase(Project project, String name, ConfigurationFactory factory) {
        super(project, factory, name);
        this.workingDirectory = runReadAction(() -> {
            List<VirtualFile> yamls = findAllShardYamls();
            VirtualFile first = yamls.isEmpty() ? null : yamls.get(0);
            return first != null ? first.toNioPath().getParent() : null;
        });
    }

    private List<VirtualFile> findAllShardYamls() {
        return List.of();
    }

    public abstract boolean getShowProgress();

    public abstract String getSuggestedNamePrefix();

    @Override
    public String suggestedName() {
        return getSuggestedNamePrefix() + (targetFile != null && targetFile.getFileName() != null ? " " + targetFile.getFileName() : "");
    }

    private Path toPathOrNull(String path) {
        try {
            return Path.of(path);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void readExternal(Element element) {
        super.readExternal(element);
        workingDirectory = JdomUtils.getNestedPath(element, "workingDirectory");
        Path tf = JdomUtils.getNestedPath(element, "targetFile");
        if (tf == null) {
            String filePath = JdomUtils.getNestedString(element, "filePath");
            targetFile = filePath != null ? toPathOrNull(filePath) : null;
        } else {
            targetFile = tf;
        }
        env = EnvironmentVariablesData.readExternal(element);
        String args = JdomUtils.getNestedString(element, "compilerArguments");
        compilerArguments = args != null ? args : "";
    }

    @Override
    public void writeExternal(Element element) {
        super.writeExternal(element);
        JdomUtils.addNestedPath(element, "workingDirectory", workingDirectory);
        JdomUtils.addNestedPath(element, "targetFile", targetFile);
        env.writeExternal(element);
        JdomUtils.addNestedValue(element, "compilerArguments", compilerArguments);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationError {
        CrystalCompiler compiler = getProject().getService(CrystalProjectWorkspaceSettings.class).getCompiler();
        if (compiler == null) throw new RuntimeConfigurationError("Crystal is not configured");
        Path file = targetFile;
        if (file == null || !Files.exists(file)) throw new RuntimeConfigurationError("Target file doesn't exist");
        if (!Files.isRegularFile(file) || !file.toString().endsWith(".cr"))
            throw new RuntimeConfigurationError("Target file is not valid");
        Path wd = workingDirectory;
        if (wd == null || !Files.exists(wd)) throw new RuntimeConfigurationError("Working directory doesn't exist");
        if (!Files.isDirectory(wd))
            throw new RuntimeConfigurationError("Working directory path refers to non-directory");
    }

    public abstract String getCommandArgument();

    public void patchArgumentList(ArrayList<Object> arguments) {
    }

    @Override
    public CrystalFileRunState getState(Executor executor, ExecutionEnvironment environment) {
        if (targetFile == null || workingDirectory == null) return null;
        ArrayList<Object> parameters = new ArrayList<>(4);

        parameters.add(getCommandArgument());

        if (getShowProgress()) {
            parameters.add("--progress");
        }

        List<String> compilerArgList = ProgramParametersConfigurator.expandMacrosAndParseParameters(compilerArguments);
        parameters.addAll(compilerArgList);

        parameters.add(targetFile.toFile());

        patchArgumentList(parameters);

        CrystalProjectWorkspaceSettings workspaceSettings = getProject().getService(CrystalProjectWorkspaceSettings.class);

        var compiler = workspaceSettings.getCompiler();
        if (compiler == null) return null;

        EnvironmentVariablesData effectiveEnv = env;
        Project project = environment.getProject();
        var vFile = StandardFileSystems.local().findFileByPath(targetFile.toString());

        try {
            var module = ModuleManager.getInstance(project).loadModule(vFile.toNioPath());
            List<VirtualFile> crystalPathRoots = module != null ? getCrystalPathRoots(module) : Collections.emptyList();
            if (!crystalPathRoots.isEmpty()) {
                String separator = (SystemInfo.isWindows) ? ";" : ":";
                String crystalPath = crystalPathRoots.stream()
                        .map(root -> compiler.getPeer().convertArgumentPath(root.getPath()))
                        .collect(Collectors.joining(separator));
                Map<String, String> envMap = new HashMap<>(env.getEnvs());
                envMap.put("CRYSTAL_PATH", crystalPath);
                effectiveEnv = env.with(envMap);
            }

            var commandLine = compiler.getPeer()
                    .buildCommandLine(parameters, effectiveEnv);
            if (commandLine == null) return null;
            commandLine = commandLine.withWorkDirectory(workingDirectory.toFile());
            return new CrystalFileRunState(commandLine, environment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ModuleWithNameAlreadyExists e) {
            throw new RuntimeException(e);
        }
    }

    List<VirtualFile> getCrystalPathRoots(Module module) {
        var psiManager = PsiManager.getInstance(getProject());
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        VirtualFile[] contentRoots = moduleRootManager.getContentRoots();
        return Stream.of(contentRoots).flatMap(vf -> {
            PsiDirectory directory = psiManager.findDirectory(vf);
            if (directory != null) {
                return Stream.of(directory, directory.findSubdirectory("src"), directory.findSubdirectory("lib"));
            } else {
                return Stream.empty();
            }
        }).map(PsiDirectory::getVirtualFile).toList();
    }
}