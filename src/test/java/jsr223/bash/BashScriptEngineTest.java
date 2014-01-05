package jsr223.bash;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
import java.io.StringReader;

import static org.junit.Assert.*;

public class BashScriptEngineTest {

    @Rule
    public OutputEater outputEater = new OutputEater();

    @Test
    public void evaluate_echo_command() throws Exception {
        Integer returnCode = (Integer) new BashScriptEngine().eval("echo hello");

        assertEquals(Bash.RETURN_CODE_OK, returnCode);
        assertEquals("hello\n", outputEater.getOut());
    }

    @Test
    public void evaluate_failing_command() throws Exception {
        Integer returnCode = (Integer) new BashScriptEngine().eval("nonexistingcommandwhatsoever");

        assertNotNull(returnCode);
        assertNotEquals(Bash.RETURN_CODE_OK, returnCode);
        assertEquals("bash: nonexistingcommandwhatsoever: command not found\n", outputEater.getErr());
    }

    @Test
    public void evaluate_use_bindings() throws Exception {
        ScriptEngine bashScriptEngine = new BashScriptEngine();

        bashScriptEngine.put("string", "aString");
        bashScriptEngine.put("integer", 42);
        bashScriptEngine.put("float", 42.0);

        Integer returnCode = (Integer) bashScriptEngine.eval("echo $string $integer $float");

        assertEquals(Bash.RETURN_CODE_OK, returnCode);
        assertEquals("aString 42 42.0\n", outputEater.getOut());
    }

    @Test
    public void evaluate_different_calls() throws Exception {
        ScriptEngine bashScriptEngine = new BashScriptEngine();

        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval("echo $string"));
        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo $string")));
    }

    @Test
    public void evaluate_different_calls_with_bindings() throws Exception {
        ScriptEngine bashScriptEngine = new BashScriptEngine();

        SimpleBindings bindings = new SimpleBindings();
        bindings.put("string", "aString");

        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval("echo $string", bindings));
        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo $string"), bindings));
        assertEquals("aString\naString\n", outputEater.getOut());
    }

    @Test
    public void evaluate_different_calls_with_context() throws Exception {
        ScriptEngine bashScriptEngine = new BashScriptEngine();

        SimpleScriptContext context = new SimpleScriptContext();
        context.setAttribute("string", "aString", ScriptContext.ENGINE_SCOPE);

        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval("echo $string", context));
        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo $string"), context));
        assertEquals("aString\naString\n", outputEater.getOut());
    }

    @Ignore("slow")
    @Test
    public void evaluate_script_with_large_output() throws Exception {
        ScriptEngine bashScriptEngine = new BashScriptEngine();

        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval("for i in $(seq 10000); do echo $i; env; done"));
        assertTrue(outputEater.getOut().contains("10000"));
    }

    @Ignore("slow")
    @Test
    public void evaluate_large_script() throws Exception {
        ScriptEngine bashScriptEngine = new BashScriptEngine();

        String largeScript = "";
        for (int i = 0; i < 5000; i++) {
            largeScript += "echo aString" + i + "\n";
        }

        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval(largeScript));
        assertTrue(outputEater.getOut().contains("aString4999"));
    }
}
