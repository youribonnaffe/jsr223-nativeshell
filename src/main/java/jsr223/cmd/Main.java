package jsr223.cmd;

import javax.script.ScriptException;

public class Main {

    public static void main(String[] args) throws ScriptException {
        String script = "";
        for (String arg : args) {
            script += arg + " ";
        }
        Object returnCode = new CmdScriptEngine().eval(script);
        System.exit((Integer) returnCode);
    }
}
