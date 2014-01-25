package jsr223.nativeshell.bash;

import jsr223.nativeshell.NativeShellRunner;
import jsr223.nativeshell.NativeShellScriptEngine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class BashScriptEngineFactory implements ScriptEngineFactory {

    private static final String NAME = "bash";
    private static final String ENGINE = "Bash interpreter";
    private static final String ENGINE_VERSION = new NativeShellRunner(new Bash()).getInstalledVersion();
    private static final String LANGUAGE = "Bash";
    private static final String LANGUAGE_VERSION = new NativeShellRunner(new Bash()).getMajorVersion();

    private static final Map<String, Object> parameters = new HashMap<String, Object>();

    static {
        parameters.put(ScriptEngine.NAME, NAME);
        parameters.put(ScriptEngine.ENGINE, ENGINE);
        parameters.put(ScriptEngine.ENGINE_VERSION, ENGINE_VERSION);
        parameters.put(ScriptEngine.LANGUAGE, LANGUAGE);
        parameters.put(ScriptEngine.LANGUAGE_VERSION, LANGUAGE_VERSION);
    }

    @Override
    public String getEngineName() {
        return NAME;
    }

    @Override
    public String getEngineVersion() {
        return ENGINE_VERSION;
    }

    @Override
    public List<String> getExtensions() {
        return asList("sh", "bash");
    }

    @Override
    public List<String> getMimeTypes() {
        return asList("application/x-sh", "application/x-bash");
    }

    @Override
    public List<String> getNames() {
        return asList("bash", "sh", "Bash");
    }

    @Override
    public String getLanguageName() {
        return LANGUAGE;
    }

    @Override
    public String getLanguageVersion() {
        return LANGUAGE_VERSION;
    }

    @Override
    public Object getParameter(String key) {
        return parameters.get(key);
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        String methodCall = m + " ";
        for (String arg : args) {
            methodCall += arg + " ";
        }
        return methodCall;
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "echo -n " + toDisplay;
    }

    @Override
    public String getProgram(String... statements) {
        String program = "#!/bin/bash\n";
        for (String statement : statements) {
            program += statement + "\n";
        }
        return program;
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new NativeShellScriptEngine(new Bash());
    }
}
