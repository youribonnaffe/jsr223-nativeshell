package jsr223.nativeshell.bash;

import jsr223.nativeshell.NativeShellRunner;
import jsr223.nativeshell.NativeShellScriptEngine;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptContext;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class BashScriptEngineTest {

    private NativeShellScriptEngine scriptEngine;
    private StringWriter scriptOutput;
    private StringWriter scriptError;

    @Before
    public void runOnlyOnLinux() {
        assumeTrue(System.getProperty("os.name").contains("Linux"));
    }

    @Before
    public void setup() {
        scriptEngine = new NativeShellScriptEngine(new Bash());
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
    public void evaluate_echo_command_no_new_line() throws Exception {
        Integer returnCode = (Integer) scriptEngine.eval("echo -n hello");

        assertEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        assertEquals("hello", scriptOutput.toString());
    }

    @Test
    public void evaluate_failing_command() throws Exception {
        Integer returnCode = (Integer) scriptEngine.eval("nonexistingcommandwhatsoever");

        assertNotNull(returnCode);
        assertNotEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        assertTrue(scriptError.toString().contains("nonexistingcommandwhatsoever: command not found\n"));
    }

    @Test
    public void evaluate_use_bindings() throws Exception {
        scriptEngine.put("string", "aString");
        scriptEngine.put("integer", 42);
        scriptEngine.put("float", 42.0);

        Integer returnCode = (Integer) scriptEngine.eval("echo $string $integer $float");

        assertEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        assertEquals("aString 42 42.0\n", scriptOutput.toString());
    }

    @Test
    public void evaluate_different_calls() throws Exception {
        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval("echo $string"));
        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval(new StringReader("echo $string")));
    }

    @Test
    public void evaluate_different_calls_with_bindings() throws Exception {
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("string", "aString");

        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval("echo $string", bindings));
        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval(new StringReader("echo $string"), bindings));
        assertEquals("aString\naString\n", scriptOutput.toString());
    }

    @Test
    public void evaluate_different_calls_with_context() throws Exception {
        SimpleScriptContext context = new SimpleScriptContext();
        context.setAttribute("string", "aString", ScriptContext.ENGINE_SCOPE);
        StringWriter contextOutput = new StringWriter();
        context.setWriter(contextOutput);

        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval("echo $string", context));
        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval(new StringReader("echo $string"), context));
        assertEquals("aString\naString\n", contextOutput.toString());
    }

    @Ignore("slow")
    @Test
    public void evaluate_script_with_large_output() throws Exception {
        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval("for i in $(seq 10000); do echo $i; env; done"));
        assertTrue(scriptOutput.toString().contains("10000"));
    }

    @Test
    public void evaluate_large_script() throws Exception {
        String largeScript = "";
        for (int i = 0; i < 10000; i++) {
            largeScript += "echo aString" + i + "\n";
        }

        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval(largeScript));
        assertTrue(scriptOutput.toString().contains("aString4999"));
    }
}
