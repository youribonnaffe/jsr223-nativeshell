package jsr223.nativeshell.executable;

import jsr223.nativeshell.IOUtils;

import javax.script.*;
import java.io.*;
import java.util.*;

import static jsr223.nativeshell.IOUtils.pipe;

public class ExecutableScriptEngine extends AbstractScriptEngine {

    @Override
    public Object eval(String script, ScriptContext scriptContext) throws ScriptException {
        try {

            String commandLineWithBindings = expandAndReplaceBindings(script, scriptContext);
            ProcessBuilder processBuilder = new ProcessBuilder(CommandLine.translateCommandline(commandLineWithBindings));

            Map<String, String> environment = processBuilder.environment();
            for (Map.Entry<String, Object> binding : scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
                environment.put(binding.getKey(), binding.getValue().toString());
            }

            final Process process = processBuilder.start();
            Thread output = readProcessOutput(process.getInputStream(), scriptContext.getWriter());
            Thread error = readProcessOutput(process.getErrorStream(), scriptContext.getErrorWriter());

            output.start();
            error.start();

            process.waitFor();
            output.join();
            error.join();

            return process.exitValue();
        } catch (Exception e) {
            throw new ScriptException(e);
        }

    }

    private String expandAndReplaceBindings(String script, ScriptContext scriptContext) {
        Bindings collectionBindings = createBindings();

        for (Map.Entry<String, Object> binding : scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
            String bindingKey = binding.getKey();
            Object bindingValue = binding.getValue();

            if (bindingValue instanceof Object[]) {
                addArrayBindingAsEnvironmentVariable(bindingKey, (Object[]) bindingValue, collectionBindings);
            } else if (bindingValue instanceof Collection) {
                addCollectionBindingAsEnvironmentVariable(bindingKey, (Collection) bindingValue, collectionBindings);
            } else if (bindingValue instanceof Map) {
                addMapBindingAsEnvironmentVariable(bindingKey, (Map<?, ?>) bindingValue, collectionBindings);
            }
        }

        scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).putAll(collectionBindings);

        Set<Map.Entry<String, Object>> bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).entrySet();

        ArrayList<Map.Entry<String, Object>> sortedBindings = new ArrayList<Map.Entry<String, Object>>(bindings);
        Collections.sort(sortedBindings, LONGER_KEY_FIRST);

        for (Map.Entry<String, Object> binding : sortedBindings) {
            String bindingKey = binding.getKey();
            Object bindingValue = binding.getValue();

            if (script.contains("$" + bindingKey)) {
                script = script.replaceAll("\\$" + bindingKey, bindingValue.toString());
            }
            if (script.contains("${" + bindingKey + "}")) {
                script = script.replaceAll("\\$\\{" + bindingKey + "\\}", bindingValue.toString());
            }
        }
        return script;
    }

    private void addMapBindingAsEnvironmentVariable(String bindingKey, Map<?, ?> bindingValue, Bindings bindings) {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) bindingValue).entrySet()) {
            bindings.put(bindingKey + "_" + entry.getKey(), (entry.getValue() == null ? "" : entry.getValue().toString()));
        }
    }

    private void addCollectionBindingAsEnvironmentVariable(String bindingKey, Collection bindingValue, Bindings bindings) {
        Object[] bindingValueAsArray = bindingValue.toArray();
        addArrayBindingAsEnvironmentVariable(bindingKey, bindingValueAsArray, bindings);
    }

    private void addArrayBindingAsEnvironmentVariable(String bindingKey, Object[] bindingValue, Bindings bindings) {
        for (int i = 0; i < bindingValue.length; i++) {
            bindings.put(bindingKey + "_" + i, (bindingValue[i] == null ? "" : bindingValue[i].toString()));
        }
    }

    private static Thread readProcessOutput(final InputStream processOutput, final Writer contextWriter) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pipe(new BufferedReader(new InputStreamReader(processOutput)), new BufferedWriter(contextWriter));
                } catch (IOException ignored) {
                }
            }
        });
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
        return new ExecutableScriptEngineFactory();
    }

    public static final Comparator<Map.Entry<String, Object>> LONGER_KEY_FIRST = new Comparator<Map.Entry<String, Object>>() {
        @Override
        public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
            return o2.getKey().length() - o1.getKey().length();
        }
    };

}
