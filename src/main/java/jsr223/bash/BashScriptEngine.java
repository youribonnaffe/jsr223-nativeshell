package jsr223.bash;

import javax.script.*;
import java.io.Reader;

public class BashScriptEngine extends AbstractScriptEngine {
    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return Bash.run(script, context.getBindings(ScriptContext.ENGINE_SCOPE)).getReturnCode();
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
        return new BashScriptEngineFactory();
    }
}
