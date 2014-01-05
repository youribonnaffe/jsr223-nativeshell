package jsr223.cmd;

import jsr223.OutputEater;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
import java.io.StringReader;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class CmdScriptEngineTest {

    @Rule
    public OutputEater outputEater = new OutputEater();

    @Before
    public void runOnlyOnWindows() {
        assumeTrue(System.getProperty("os.name").contains("Windows"));
    }

    @Test
    public void evaluate_echo_command() throws Exception {
        Integer returnCode = (Integer) new CmdScriptEngine().eval("echo hello");

        assertEquals(Cmd.RETURN_CODE_OK, returnCode);
        assertEquals("hello\n", outputEater.getOut());
    }

    @Test
    public void evaluate_failing_command() throws Exception {
        Integer returnCode = (Integer) new CmdScriptEngine().eval("nonexistingcommandwhatsoever");

        assertNotNull(returnCode);
        assertNotEquals(Cmd.RETURN_CODE_OK, returnCode);
        assertEquals("cmd: nonexistingcommandwhatsoever: command not found\n", outputEater.getErr());
    }

    @Test
    public void evaluate_use_bindings() throws Exception {
        ScriptEngine bashScriptEngine = new CmdScriptEngine();

        bashScriptEngine.put("string", "aString");
        bashScriptEngine.put("integer", 42);
        bashScriptEngine.put("float", 42.0);

        Integer returnCode = (Integer) bashScriptEngine.eval("echo %string% %integer% %float%");

        assertEquals(Cmd.RETURN_CODE_OK, returnCode);
        assertEquals("aString 42 42.0\n", outputEater.getOut());
    }

    @Test
    public void evaluate_different_calls() throws Exception {
        ScriptEngine bashScriptEngine = new CmdScriptEngine();

        assertEquals(Cmd.RETURN_CODE_OK, bashScriptEngine.eval("echo %string%"));
        assertEquals(Cmd.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo %string%")));
    }

    @Test
    public void evaluate_different_calls_with_bindings() throws Exception {
        ScriptEngine bashScriptEngine = new CmdScriptEngine();

        SimpleBindings bindings = new SimpleBindings();
        bindings.put("string", "aString");

        assertEquals(Cmd.RETURN_CODE_OK, bashScriptEngine.eval("echo %string%", bindings));
        assertEquals(Cmd.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo %string%"), bindings));
        assertEquals("aString\naString\n", outputEater.getOut());
    }

    @Test
    public void evaluate_different_calls_with_context() throws Exception {
        ScriptEngine bashScriptEngine = new CmdScriptEngine();

        SimpleScriptContext context = new SimpleScriptContext();
        context.setAttribute("string", "aString", ScriptContext.ENGINE_SCOPE);

        assertEquals(Cmd.RETURN_CODE_OK, bashScriptEngine.eval("echo %string%", context));
        assertEquals(Cmd.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo %string%"), context));
        assertEquals("aString\naString\n", outputEater.getOut());
    }

    @Ignore("slow")
    @Test
    public void evaluate_script_with_large_output() throws Exception {
        ScriptEngine bashScriptEngine = new CmdScriptEngine();

        assertEquals(Cmd.RETURN_CODE_OK, bashScriptEngine.eval("FOR /L %%G IN (1,1,10000) DO echo %%G"));
        assertTrue(outputEater.getOut().contains("10000"));
    }

    @Ignore("slow")
    @Test
    public void evaluate_large_script() throws Exception {
        ScriptEngine bashScriptEngine = new CmdScriptEngine();

        String largeScript = "";
        for (int i = 0; i < 5000; i++) {
            largeScript += "echo aString" + i + "\n";
        }

        assertEquals(Cmd.RETURN_CODE_OK, bashScriptEngine.eval(largeScript));
        assertTrue(outputEater.getOut().contains("aString4999"));
    }
}
