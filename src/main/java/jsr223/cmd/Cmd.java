package jsr223.cmd;

import jsr223.IOUtils;

import javax.script.Bindings;
import java.io.*;
import java.util.Map;

public class Cmd {

    public static final Integer RETURN_CODE_OK = 0;

    public static String getInstalledVersion() {
        try {
            return runSilent("echo|set /p=%CmdExtVersion%").getOutput();
        } catch (Throwable e) {
            return "Could not determine version";
        }
    }

    public static String getMajorVersion() {
        return getInstalledVersion();
    }

    public static BashCommand runSilent(String command) {
        ProcessBuilder processBuilder = createCmdProcess(command);
        try {
            Process process = processBuilder.start();

            process.waitFor();
            return new BashCommand(process);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BashCommand run(String command, Bindings bindings) {
        ProcessBuilder processBuilder = createCmdProcess(command);

        Map<String, String> environment = processBuilder.environment();
        for (Map.Entry<String, Object> binding : bindings.entrySet()) {
            environment.put(binding.getKey(), binding.getValue().toString());
        }

        try {
            Process process = processBuilder.start();

            stream(process.getInputStream(), redirectOutput);
            stream(process.getErrorStream(), redirectErr);

            process.waitFor();
            return new BashCommand(process);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ProcessBuilder createCmdProcess(String command) {
        return new ProcessBuilder("jsr223/cmd/cmd", "/c", command);
    }

    private static void stream(InputStream input, PrintStream output) throws IOException {
        BufferedReader processOutput = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = processOutput.readLine()) != null) {
            output.println(line);
        }
    }

    public static class BashCommand {
        private Process process;

        public BashCommand(Process process) {
            this.process = process;
        }

        public String getOutput() {
            InputStream processOutput = process.getInputStream();
            return IOUtils.toString(processOutput);
        }

        public Integer getReturnCode() {
            return process.exitValue();
        }
    }

    // protected for testing only
    static PrintStream redirectOutput = System.out;
    // protected for testing only
    static PrintStream redirectErr = System.err;

}
