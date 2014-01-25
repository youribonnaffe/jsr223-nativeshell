package jsr223.nativeshell.cmd;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static org.junit.Assert.assertNotNull;

public class CmdScriptEngineFactoryTest {

    @Test
    public void testCmdScriptEngineIsFound() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

        assertNotNull(scriptEngineManager.getEngineByExtension("bat"));
        assertNotNull(scriptEngineManager.getEngineByName("cmd"));
        assertNotNull(scriptEngineManager.getEngineByMimeType("application/x-cmd"));
        assertNotNull(scriptEngineManager.getEngineByMimeType("application/x-bat"));
    }

    @Test
    public void testCmdScriptEngineVersions() {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByExtension("bat");

        assertNotNull(scriptEngine.getFactory().getEngineVersion());
        assertNotNull(scriptEngine.getFactory().getLanguageVersion());
    }
}
