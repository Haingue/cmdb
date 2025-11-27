package com.management.cmdb.backend.scripting.engine;

import com.management.cmdb.backend.scripting.ScriptException;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.Map;

public class LuaEngine implements ScriptEngine {
    @Override
    public Object execute(String script, Map<String, Object> context) throws ScriptException {
        try {
            Globals globals = JsePlatform.standardGlobals();
            LuaValue _G = globals.get("_G");

            context.forEach((key, value) -> {
                if (value instanceof Number) {
                    _G.set(key, LuaValue.valueOf(((Number) value).doubleValue()));
                } else if (value instanceof String) {
                    _G.set(key, LuaValue.valueOf((String) value));
                } else if (value instanceof Boolean) {
                    _G.set(key, LuaValue.valueOf((Boolean) value));
                }
            });

            LuaValue chunk = globals.load(script);
            return chunk.call();
        } catch (LuaError e) {
            throw new ScriptException("Error Lua: " + e.getMessage(), e);
        }
    }
}
