package com.management.cmdb.backend.scripting.engine;

import com.management.cmdb.backend.scripting.ScriptLanguage;
import com.management.cmdb.backend.scripting.ScriptException;
import org.springframework.stereotype.Component;

@Component
public class ScriptEngineFactory {

    private ScriptEngineFactory() {}

    public ScriptEngine getEngine(ScriptLanguage language) {
        return switch (language) {
            case JYTHON -> new JythonEngine();
            case LUA -> new LuaEngine();
            default -> throw new ScriptException("Unsupported language " + language);
        };
    }

}
