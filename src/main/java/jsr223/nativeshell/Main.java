package jsr223.nativeshell;

import jsr223.nativeshell.bash.Bash;
import jsr223.nativeshell.cmd.Cmd;

import javax.script.ScriptException;

public class Main {

    public static void main(String[] args) throws ScriptException {
        NativeShell shell = null;
        if ("cmd".equals(args[0])) {
            shell = new Cmd();
        } else if ("bash".equals(args[0])) {
            shell = new Bash();
        } else {
            System.err.println("First argument must be shell name (cmd/bash)");
            System.exit(-1);
        }

        String script = "";
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            script += arg + " ";
        }

        Object returnCode = new NativeShellScriptEngine(shell).eval(script);
        System.exit((Integer) returnCode);
    }
}
