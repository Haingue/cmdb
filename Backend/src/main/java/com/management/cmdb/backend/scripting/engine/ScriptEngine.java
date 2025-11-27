package com.management.cmdb.backend.scripting.engine;

import com.management.cmdb.backend.scripting.ScriptException;

import java.util.Map;

public interface ScriptEngine {
    Object execute(String script, Map<String, Object> context) throws ScriptException;
}
