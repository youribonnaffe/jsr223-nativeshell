package jsr223.nativeshell;

import javax.script.ScriptContext;
import java.io.*;
import java.util.Map;

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
            environment.put(binding.getKey(), binding.getValue().toString());
        }
    }

    private static Thread readProcessOutput(final InputStream processOutput, final Writer contextWriter) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(processOutput));
                try {
                    char[] buff = new char[1024];
                    int n = reader.read(buff);
                    while (n != -1) {
                        BufferedWriter bufferedWriter = new BufferedWriter(contextWriter);
                        bufferedWriter.write(buff, 0, n);
                        bufferedWriter.flush();
                        n = reader.read(buff);
                    }
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        });
    }

    private static File commandAsTemporaryFile(String command) {
        try {
            File commandAsFile = File.createTempFile("jsr223nativeshell-", ".sh");
            IOUtils.writeStringToFile(command, commandAsFile);
            return commandAsFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
