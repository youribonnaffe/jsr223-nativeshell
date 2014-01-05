package jsr223.bash;

import javax.script.ScriptException;

public class Main {

    public static void main(String[] args) throws ScriptException {
        String script = "";
        for (String arg : args) {
            script += arg + " ";
        }
        Object returnCode = new BashScriptEngine().eval(script);
        System.exit((Integer) returnCode);
    }
}
