package org.intellij.crystal;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.io.BaseOutputReader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Function;

public class ProcessUtilsExt {
    private static final Logger log = Logger.getInstance("org.crystal.intellij.util.ProcessUtilsExt");

    public static class CrProcessExecutionException extends RuntimeException {
        public CrProcessExecutionException(String message) {
            super(message);
        }

        public CrProcessExecutionException(Throwable cause) {
            super(cause);
        }

        public static class Start extends CrProcessExecutionException {
            public Start(ExecutionException cause) {
                super(cause);
            }
        }

        public static class Cancelled extends CrProcessExecutionException {
            public Cancelled(String commandLineString, ProcessOutput output) {
                super(errorMessage(commandLineString, output));
            }
        }

        public static class Timeout extends CrProcessExecutionException {
            public Timeout(String commandLineString, ProcessOutput output) {
                super(errorMessage(commandLineString, output));
            }
        }

        public static class Aborted extends CrProcessExecutionException {
            public Aborted(String commandLineString, ProcessOutput output) {
                super(errorMessage(commandLineString, output));
            }
        }

        public static String errorMessage(String commandLineString, ProcessOutput output) {
            return "Execution failed (exit code " + output.getExitCode() + ").\n"
                    + commandLineString + "\n"
                    + "stdout : " + output.getStdout() + "\n"
                    + "stderr : " + output.getStderr();
        }
    }

    public static class CrCapturingProcessHandler extends CapturingProcessHandler {
        public CrCapturingProcessHandler(GeneralCommandLine commandLine) throws ExecutionException {
            super(commandLine);
        }

        @Override
        protected BaseOutputReader.Options readerOptions() {
            return BaseOutputReader.Options.BLOCKING;
        }

        public static CrResult<CrCapturingProcessHandler, ExecutionException> startProcess(GeneralCommandLine commandLine) {
            try {
                return CrResult.Ok(new CrCapturingProcessHandler(commandLine));
            } catch (ExecutionException e) {
                return CrResult.Err(e);
            }
        }
    }

    public static CrResult<ProcessOutput, CrProcessExecutionException> execute(
            @NotNull GeneralCommandLine commandLine,
            @Nullable byte[] stdIn,
            @NotNull Function<CapturingProcessHandler, ProcessOutput> runner
    ) {
        log.info("Executing `" + commandLine.getCommandLineString() + "`");

        CrResult<CrCapturingProcessHandler, ExecutionException> handlerResult = CrCapturingProcessHandler.startProcess(commandLine);
        if (handlerResult.isErr()) {
            log.warn("Failed to run executable", handlerResult.getErr());
            return CrResult.Err(new CrProcessExecutionException.Start(handlerResult.getErr()));
        }
        CrCapturingProcessHandler handler = handlerResult.getOk();

        if (stdIn != null) {
            try {
                handler.getProcessInput().write(stdIn);
                handler.getProcessInput().close();
            } catch (IOException e) {
                return CrResult.Err(new CrProcessExecutionException(e));
            }
        }

        ProcessOutput output = runner.apply(handler);
        if (output.isCancelled()) {
            return CrResult.Err(new CrProcessExecutionException.Cancelled(commandLine.getCommandLineString(), output));
        } else if (output.isTimeout()) {
            return CrResult.Err(new CrProcessExecutionException.Timeout(commandLine.getCommandLineString(), output));
        } else if (output.getExitCode() != 0) {
            return CrResult.Err(new CrProcessExecutionException.Aborted(commandLine.getCommandLineString(), output));
        } else {
            return CrResult.Ok(output);
        }
    }

    // Simple Result type for Java
    public static class CrResult<T, E> {
        private final T ok;
        private final E err;

        public CrResult(T ok, E err) {
            this.ok = ok;
            this.err = err;
        }

        public static <T, E> CrResult<T, E> Ok(T value) {
            return new CrResult<>(value, null);
        }

        public static <T, E> CrResult<T, E> Err(E error) {
            return new CrResult<>(null, error);
        }

        public boolean isOk() {
            return ok != null;
        }

        public boolean isErr() {
            return err != null;
        }

        public T getOk() {
            return ok;
        }

        public E getErr() {
            return err;
        }
    }
}