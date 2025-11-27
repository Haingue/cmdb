package com.management.cmdb.backend.scripting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserScriptServiceTest {

    @Autowired
    private UserScriptService userScriptService;

    @Test
    void shouldSaveAndRetrieveScript() {
        UserScript script = new UserScript();
        script.setName("Test Script");
        script.setLanguage(ScriptLanguage.JYTHON);
        script.setContent("print('Hello')");
        script.setAuthor("test-user");

        UserScript saved = userScriptService.saveScript(script);
        UserScript found = userScriptService.getScriptById(saved.getId());

        assertEquals("Test Script", found.getName());
        assertEquals(ScriptLanguage.JYTHON, found.getLanguage());
    }
}