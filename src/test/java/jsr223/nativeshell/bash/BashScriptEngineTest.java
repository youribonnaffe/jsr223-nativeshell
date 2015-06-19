package jsr223.nativeshell.bash;

import jsr223.nativeshell.NativeShellRunner;
import jsr223.nativeshell.NativeShellScriptEngine;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.*;
import java.io.StringReader;
import java.io.StringWriter;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
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

    @Test(expected = ScriptException.class)
    public void evaluate_failing_command() throws Exception {
        scriptEngine.eval("nonexistingcommandwhatsoever");
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
    public void evaluate_use_bindings_arrays() throws Exception {
        scriptEngine.put("array", new String[]{"oneString", "anotherString", "thenAString"});
        scriptEngine.put("array_empty", new String[0]);
        scriptEngine.put("array_nulls", new String[]{null, null});

        Integer returnCode = (Integer) scriptEngine.eval("echo $array_0 $array_1 $array_2 $array_empty_0 $array_nulls_0");

        assertEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        assertEquals("oneString anotherString thenAString\n", scriptOutput.toString());
    }

    @Test
    public void evaluate_use_bindings_lists() throws Exception {
        scriptEngine.put("list", asList("oneString", "anotherString", "thenAString"));
        scriptEngine.put("list_empty", emptyList());
        scriptEngine.put("list_nulls", asList(null, null));

        Integer returnCode = (Integer) scriptEngine.eval("echo $list_0 $list_1 $list_2 $list_empty_0 $list_nulls_0");

        assertEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        assertEquals("oneString anotherString thenAString\n", scriptOutput.toString());
    }

    @Test
    public void evaluate_use_bindings_maps() throws Exception {
        scriptEngine.put("map", singletonMap("key", "value"));
        scriptEngine.put("map_empty", emptyMap());
        scriptEngine.put("map_nulls", singletonMap("key", null));

        Integer returnCode = (Integer) scriptEngine.eval("echo $map_key $map_empty_key $map_nulls_key");

        assertEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        assertEquals("value\n", scriptOutput.toString());
    }

    @Test
    public void evaluate_different_calls() throws Exception {
        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval("echo $string"));
        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval(new StringReader("echo $string")));
    }

    @Test
    public void null_binding() throws Exception {
        Bindings bindings = scriptEngine.createBindings();
        bindings.put("var", null);

        scriptEngine.eval("echo $var", bindings);

        assertEquals("\n", scriptOutput.toString());
    }

    @Test
    public void reading_input() throws Exception {
        StringReader stringInput = new StringReader("hello\n");
        scriptEngine.getContext().setReader(stringInput);
        stringInput.close();
        assertEquals(NativeShellRunner.RETURN_CODE_OK, scriptEngine.eval("cat"));
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
