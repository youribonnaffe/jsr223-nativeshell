package jsr223.nativeshell;

import javax.script.ScriptEngineFactory;
import java.io.File;

public interface NativeShell {
    public ProcessBuilder createProcess(File commandAsFile);

    public ProcessBuilder createProcess(String command);

    public String getInstalledVersionCommand();

    public String getMajorVersionCommand();

    ScriptEngineFactory getScriptEngineFactory();

    String getFileExtension();
}
