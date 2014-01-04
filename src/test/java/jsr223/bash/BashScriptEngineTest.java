package jsr223.bash;

import org.junit.Rule;
import org.junit.Test;

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
        BashScriptEngine bashScriptEngine = new BashScriptEngine();

        bashScriptEngine.put("string", "aString");
        bashScriptEngine.put("integer", 42);
        bashScriptEngine.put("float", 42.0);

        Integer returnCode = (Integer) bashScriptEngine.eval("echo $string $integer $float");

        assertEquals(Bash.RETURN_CODE_OK, returnCode);
        assertEquals("aString 42 42.0\n", outputEater.getOut());
    }

    @Test
    public void evaluate_different_calls() throws Exception {
        BashScriptEngine bashScriptEngine = new BashScriptEngine();

        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval("echo $string"));
        assertEquals(Bash.RETURN_CODE_OK, bashScriptEngine.eval(new StringReader("echo $string")));
    }
}
