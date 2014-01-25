package jsr223.nativeshell.cmd;

import jsr223.nativeshell.NativeShellRunner;
import jsr223.nativeshell.NativeShellScriptEngine;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class CmdScriptEngineTest {

    private NativeShellScriptEngine scriptEngine;
    private StringWriter scriptOutput;
    private StringWriter scriptError;

    @Before
    public void runOnlyOnWindows() {
        assumeTrue(System.getProperty("os.name").contains("Windows"));
    }

    @Before
    public void setup() {
        scriptEngine = new NativeShellScriptEngine(new Cmd());
        scriptOutput = new StringWriter();
        scriptEngine.getContext().setWriter(scriptOutput);
        scriptError = new StringWriter();
        scriptEngine.getContext().setErrorWriter(scriptError);
    }

    @Test
    public void evaluate_echo_command() throws Exception {
        Integer returnCode = (Integer) scriptEngine.eval("echo hello");

        assertEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        assertEquals("hello\n", scriptOutput.toString());
    }

    @Test
    public void evaluate_failing_command() throws Exception {
        Integer returnCode = (Integer) scriptEngine.eval("nonexistingcommandwhatsoever");

        assertNotNull(returnCode);
        assertNotEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        assertEquals("cmd: nonexistingcommandwhatsoever: command not found\n", scriptError.toString());
    }

    @Test
    public void evaluate_use_bindings() throws Exception {
        ScriptEngine bashScriptEngine = scriptEngine;

        bashScriptEngine.put("string", "aString");
        bashScriptEngine.put("integer", 42);
        bashScriptEngine.put("float", 42.0);

        Integer returnCode = (Integer) bashScriptEngine.eval("echo %string% %integer% %float%");

        assertEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        assertEquals("aString 42 42.0\n", scriptOutput.toString());
    }

    @Test
    public void evaluate_different_calls() throws Exception {
        ScriptEngine bashScriptEngine = scriptEngine;

        assertEquals(NativeShellRunner.RETURN_CODE_OK, bashScriptEngine.eval("echo %string%"));
        assertEquals(NativeShellRunner.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo %string%")));
    }

    @Test
    public void evaluate_different_calls_with_bindings() throws Exception {
        ScriptEngine bashScriptEngine = scriptEngine;

        SimpleBindings bindings = new SimpleBindings();
        bindings.put("string", "aString");

        assertEquals(NativeShellRunner.RETURN_CODE_OK, bashScriptEngine.eval("echo %string%", bindings));
        assertEquals(NativeShellRunner.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo %string%"), bindings));
        assertEquals("aString\naString\n", scriptOutput.toString());
    }

    @Test
    public void evaluate_different_calls_with_context() throws Exception {
        ScriptEngine bashScriptEngine = scriptEngine;

        SimpleScriptContext context = new SimpleScriptContext();
        context.setAttribute("string", "aString", ScriptContext.ENGINE_SCOPE);

        assertEquals(NativeShellRunner.RETURN_CODE_OK, bashScriptEngine.eval("echo %string%", context));
        assertEquals(NativeShellRunner.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo %string%"), context));
        assertEquals("aString\naString\n", scriptOutput.toString());
    }

    @Ignore("slow")
    @Test
    public void evaluate_script_with_large_output() throws Exception {
        ScriptEngine bashScriptEngine = scriptEngine;

        assertEquals(NativeShellRunner.RETURN_CODE_OK, bashScriptEngine.eval("FOR /L %%G IN (1,1,10000) DO echo %%G"));
        assertTrue(scriptOutput.toString().contains("10000"));
    }

    @Ignore("slow")
    @Test
    public void evaluate_large_script() throws Exception {
        ScriptEngine bashScriptEngine = scriptEngine;

        String largeScript = "";
        for (int i = 0; i < 5000; i++) {
            largeScript += "echo aString" + i + "\n";
        }

        assertEquals(NativeShellRunner.RETURN_CODE_OK, bashScriptEngine.eval(largeScript));
        assertTrue(scriptOutput.toString().contains("aString4999"));
    }
}
