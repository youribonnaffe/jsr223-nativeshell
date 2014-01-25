package jsr223.nativeshell.bash;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static org.junit.Assert.assertNotNull;

public class BashScriptEngineFactoryTest {

    @Test
    public void testBashScriptEngineIsFound() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

        assertNotNull(scriptEngineManager.getEngineByExtension("sh"));
        assertNotNull(scriptEngineManager.getEngineByName("bash"));
        assertNotNull(scriptEngineManager.getEngineByMimeType("application/x-bash"));
        assertNotNull(scriptEngineManager.getEngineByMimeType("application/x-sh"));
    }

    @Test
    public void testBashScriptEngineVersions() {
        ScriptEngine bashScriptEngine = new ScriptEngineManager().getEngineByExtension("sh");

        assertNotNull(bashScriptEngine.getFactory().getEngineVersion());
        assertNotNull(bashScriptEngine.getFactory().getLanguageVersion());
    }
}
