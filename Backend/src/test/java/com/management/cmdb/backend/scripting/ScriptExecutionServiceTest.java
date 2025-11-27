package com.management.cmdb.backend.scripting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScriptExecutionServiceTest {

    @Autowired
    private ScriptExecutionService scriptExecutionService;

    @Autowired
    private UserScriptService userScriptService;

    @Test
    void shouldExecuteJythonScript() throws ScriptException {
        UserScript script = new UserScript();
        script.setName("Test Jython");
        script.setLanguage(ScriptLanguage.JYTHON);
        script.setContent("""
                count=0
                for _ in range(10):
                  count=count+1
                return count
                """);
        script.setAuthor("test");

        UserScript saved = userScriptService.saveScript(script);
        Object result = scriptExecutionService.executeScript(saved);

        assertEquals("10", result.toString());
    }

    @Test
    void shouldExecuteLuaScript() throws ScriptException {
        UserScript script = new UserScript();
        script.setName("Test Lua");
        script.setLanguage(ScriptLanguage.LUA);
        script.setContent("""
                local count = 0
                for _ = 1, 10 do
                    count = count + 1
                end
                return count
                """);
        script.setAuthor("test");

        UserScript saved = userScriptService.saveScript(script);
        Object result = scriptExecutionService.executeScript(saved);

        assertEquals("10", result.toString());
    }
}