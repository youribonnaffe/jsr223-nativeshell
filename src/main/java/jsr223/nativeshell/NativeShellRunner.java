package jsr223.nativeshell;

import javax.script.ScriptContext;
import java.io.*;
import java.util.Collection;
import java.util.Map;

import static jsr223.nativeshell.IOUtils.pipe;

public class NativeShellRunner {

    public static final Integer RETURN_CODE_OK = 0;

    private NativeShell nativeShell;

    public NativeShellRunner(NativeShell nativeShell) {
        this.nativeShell = nativeShell;
    }

    public String getInstalledVersion() {
        try {
            return runAndGetOutput(nativeShell.getInstalledVersionCommand());
        } catch (Throwable e) {
            return "Could not determine version";
        }
    }

    public String getMajorVersion() {
        try {
            return runAndGetOutput(nativeShell.getMajorVersionCommand());
        } catch (Throwable e) {
            return "Could not determine version";
        }
    }

    public int run(String command, ScriptContext scriptContext) {
        File commandAsTemporaryFile = commandAsTemporaryFile(command);
        int exitValue = run(commandAsTemporaryFile, scriptContext);
        commandAsTemporaryFile.delete();
        return exitValue;
    }

    private int run(File command, ScriptContext scriptContext) {
        ProcessBuilder processBuilder = nativeShell.createProcess(command);

        addBindingsAsEnvironmentVariables(scriptContext, processBuilder);

        return run(processBuilder, scriptContext.getWriter(), scriptContext.getErrorWriter());
    }

    private String runAndGetOutput(String command) {
        ProcessBuilder processBuilder = nativeShell.createProcess(command);
        StringWriter processOutput = new StringWriter();
        run(processBuilder, processOutput, new StringWriter());
        return processOutput.toString();
    }

    private static int run(ProcessBuilder processBuilder, Writer processOutput, Writer processError) {
        try {
            final Process process = processBuilder.start();
            Thread output = readProcessOutput(process.getInputStream(), processOutput);
            Thread error = readProcessOutput(process.getErrorStream(), processError);

            output.start();
            error.start();

            process.waitFor();
            output.join();
            error.join();

            return process.exitValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addBindingsAsEnvironmentVariables(ScriptContext scriptContext, ProcessBuilder processBuilder) {
        Map<String, String> environment = processBuilder.environment();
        for (Map.Entry<String, Object> binding : scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
            String bindingKey = binding.getKey();
            Object bindingValue = binding.getValue();

            if (bindingValue instanceof Object[]) {
                addArrayBindingAsEnvironmentVariable(bindingKey, (Object[]) bindingValue, environment);
            } else if (bindingValue instanceof Collection) {
                addCollectionBindingAsEnvironmentVariable(bindingKey, (Collection) bindingValue, environment);
            } else if (bindingValue instanceof Map) {
                addMapBindingAsEnvironmentVariable(bindingKey, (Map<?, ?>) bindingValue, environment);
            } else {
                environment.put(bindingKey, bindingValue.toString());
            }
        }
    }

    private void addMapBindingAsEnvironmentVariable(String bindingKey, Map<?, ?> bindingValue, Map<String, String> environment) {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) bindingValue).entrySet()) {
            environment.put(bindingKey + "_" + entry.getKey(), (entry.getValue() == null ? "" : entry.getValue().toString()));
        }
    }

    private void addCollectionBindingAsEnvironmentVariable(String bindingKey, Collection bindingValue, Map<String, String> environment) {
        Object[] bindingValueAsArray = bindingValue.toArray();
        addArrayBindingAsEnvironmentVariable(bindingKey, bindingValueAsArray, environment);
    }

    private void addArrayBindingAsEnvironmentVariable(String bindingKey, Object[] bindingValue, Map<String, String> environment) {
        for (int i = 0; i < bindingValue.length; i++) {
            environment.put(bindingKey + "_" + i, (bindingValue[i] == null ? "" : bindingValue[i].toString()));
        }
    }

    private static Thread readProcessOutput(final InputStream processOutput, final Writer contextWriter) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pipe(new BufferedReader(new InputStreamReader(processOutput)), new BufferedWriter(contextWriter));
                } catch (IOException ignored) {
                }
            }
        });
    }

    private File commandAsTemporaryFile(String command) {
        try {
            File commandAsFile = File.createTempFile("jsr223nativeshell-", nativeShell.getFileExtension());
            IOUtils.writeStringToFile(command, commandAsFile);
            return commandAsFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
