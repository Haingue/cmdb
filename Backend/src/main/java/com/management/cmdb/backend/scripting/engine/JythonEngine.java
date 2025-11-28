package com.management.cmdb.backend.scripting.engine;

import com.management.cmdb.backend.scripting.ScriptException;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import java.util.Map;

public class JythonEngine implements ScriptEngine {

    private static String RESULT_KEY = "output";

    @Override
    public Object execute(String script, Map<String, Object> context) throws ScriptException {
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
            context.forEach(pyInterp::set);

            script = script.replace("return", RESULT_KEY+"=");
            pyInterp.exec(script);
            return pyInterp.get(RESULT_KEY);
        } catch (PyException e) {
            throw new ScriptException("Error Jython: " + e.getMessage(), e);
        }
    }
}
