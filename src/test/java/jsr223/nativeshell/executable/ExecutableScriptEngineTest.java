package jsr223.nativeshell.executable;

import java.io.StringWriter;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

public class ExecutableScriptEngineTest {

    private ExecutableScriptEngine scriptEngine;
    private StringWriter scriptOutput;
    private StringWriter scriptError;

    @Before
    public void setup() {
        scriptEngine = new ExecutableScriptEngine();
        scriptOutput = new StringWriter();
        scriptEngine.getContext().setWriter(scriptOutput);
        scriptError = new StringWriter();
        scriptEngine.getContext().setErrorWriter(scriptError);
    }

    @Test
    public void simple_executable() throws Exception {
        Object result = scriptEngine.eval("hostname");

        assertEquals(0, result);
        assertNotEquals("", scriptOutput.toString());
        assertEquals("", scriptError.toString());
    }

    @Test
    public void with_args() throws Exception {
        Object result = scriptEngine.eval("echo hello");

        assertEquals(0, result);
        assertEquals("hello\n", scriptOutput.toString());
        assertEquals("", scriptError.toString());
    }

    @Test
    public void quoted_args() throws Exception {
        Object result = scriptEngine.eval("echo \"hello; bob\"");

        assertEquals(0, result);
        assertEquals("hello; bob\n", scriptOutput.toString());
        assertEquals("", scriptError.toString());
    }

    @Test(expected = ScriptException.class)
    public void non_existing_command() throws Exception {
        scriptEngine.eval("blawhhhhhh");
    }

    @Test(expected = ScriptException.class)
    public void error_returned() throws Exception {
        scriptEngine.eval("false");
    }

    @Test
    public void bindings() throws Exception {
        Bindings bindings = scriptEngine.createBindings();
        bindings.put("var", "value");
        bindings.put("another", "foo");

        scriptEngine.eval("echo $var ${another} $not_existing", bindings);

        assertEquals("value foo $not_existing\n", scriptOutput.toString());
    }

    @Test
    public void number_bindings() throws Exception {
        Bindings bindings = scriptEngine.createBindings();
        bindings.put("int", 42);
        bindings.put("float", 42.0);

        scriptEngine.eval("echo $int ${float}", bindings);

        assertEquals("42 42.0\n", scriptOutput.toString());
    }

    @Test
    public void collection_bindings() throws Exception {
        Bindings bindings = scriptEngine.createBindings();
        bindings.put("array", new String[]{"one", "two"});
        bindings.put("long_array", "a a a a a a a a a a b".split(" "));
        bindings.put("list", singletonList("l1"));
        bindings.put("map", singletonMap("key", "value"));

        scriptEngine.eval("echo $array_0 $array_1 $list_0 $map_key $long_array_10", bindings);

        assertEquals("one two l1 value b\n", scriptOutput.toString());
    }

    @Test
    public void environment_bindings() throws Exception {
        Bindings bindings = scriptEngine.createBindings();
        bindings.put("var", "foo");

        scriptEngine.eval("printenv var", bindings);

        assertEquals("foo\n", scriptOutput.toString());
    }
}