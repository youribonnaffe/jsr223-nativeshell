package jsr223.nativeshell.bash;

import jsr223.nativeshell.NativeShell;

import javax.script.ScriptEngineFactory;
import java.io.File;

public class Bash implements NativeShell {

    public ProcessBuilder createProcess(File commandAsFile) {
        return new ProcessBuilder("bash", commandAsFile.getAbsolutePath());
    }

    public ProcessBuilder createProcess(String command) {
        return new ProcessBuilder("bash", "-c", command);
    }

    @Override
    public String getInstalledVersionCommand() {
        return "echo -n $BASH_VERSION";
    }

    @Override
    public String getMajorVersionCommand() {
        return "echo -n $BASH_VERSINFO";
    }

    @Override
    public ScriptEngineFactory getScriptEngineFactory() {
        return new BashScriptEngineFactory();
    }

    @Override
    public String getFileExtension() {
        return ".sh";
    }
}
