package com.management.cmdb.backend.scripting;

import com.management.cmdb.backend.scripting.engine.ScriptEngine;
import com.management.cmdb.backend.scripting.engine.ScriptEngineFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class ScriptExecutionService {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final ScriptEngineFactory engineFactory;
    private final ScriptFunctions scriptFunctions;
    private final int EXECUTION_TIMEOUT = 3;

    public ScriptExecutionService(ScriptEngineFactory engineFactory, ScriptFunctions scriptFunctions) {
        this.engineFactory = engineFactory;
        this.scriptFunctions = scriptFunctions;
    }

    public Object executeScript(UserScript script) throws ScriptException {
        ScriptEngine engine = engineFactory.getEngine(script.getLanguage());
        Map<String, Object> context = scriptFunctions.getAvailableFunctions();
        return executeWithTimeout(engine, script.getContent(), context);
    }

    public Object executeWithTimeout(
            ScriptEngine engine, String script, Map<String, Object> context
    ) throws ScriptException {
        Future<Object> future = executor.submit(() ->
                engine.execute(script, context)
        );

        try {
            return future.get(EXECUTION_TIMEOUT, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new ScriptException("Timeout: script running timed out");
        } catch (Exception e) {
            throw new ScriptException("Running error: " + e.getMessage(), e);
        }
    }
}
