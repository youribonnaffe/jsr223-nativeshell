package jsr223.cmd;

import jsr223.IOUtils;

import javax.script.*;
import java.io.Reader;

public class CmdScriptEngine extends AbstractScriptEngine {
    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return Cmd.run(script, context.getBindings(ScriptContext.ENGINE_SCOPE)).getReturnCode();
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
        return new CmdScriptEngineFactory();
    }
}
