package jsr223.nativeshell;

import javax.script.*;
import java.io.Reader;

public class NativeShellScriptEngine extends AbstractScriptEngine {

    private NativeShell nativeShell;

    public NativeShellScriptEngine(NativeShell nativeShell) {
        this.nativeShell = nativeShell;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return new NativeShellRunner(nativeShell).run(script, context);
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return eval(IOUtils.toString(reader), context);
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return nativeShell.getScriptEngineFactory();
    }
}
