package jsr223.nativeshell.cmd;

import jsr223.nativeshell.NativeShell;

import javax.script.ScriptEngineFactory;
import java.io.File;

public class Cmd implements NativeShell {

    public ProcessBuilder createProcess(File commandAsFile) {
        return new ProcessBuilder("cmd", "/q", "/c", commandAsFile.getAbsolutePath());
    }

    public ProcessBuilder createProcess(String command) {
        return new ProcessBuilder("cmd", "/c", command);
    }

    @Override
    public String getInstalledVersionCommand() {
        return "echo|set /p=%CmdExtVersion%";
    }

    @Override
    public String getMajorVersionCommand() {
        return getInstalledVersionCommand();
    }

    @Override
    public ScriptEngineFactory getScriptEngineFactory() {
        return new CmdScriptEngineFactory();
    }

    @Override
    public String getFileExtension() {
        return ".bat";
    }

}
