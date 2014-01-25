package jsr223.nativeshell.cmd;

import jsr223.nativeshell.NativeShellScriptEngine;

import javax.script.ScriptException;

public class Main {

    public static void main(String[] args) throws ScriptException {
        String script = "";
        for (String arg : args) {
            script += arg + " ";
        }
        Object returnCode = new NativeShellScriptEngine(new Cmd()).eval(script);
        System.exit((Integer) returnCode);
    }
}
