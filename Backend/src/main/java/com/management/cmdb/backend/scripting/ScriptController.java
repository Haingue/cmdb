package com.management.cmdb.backend.scripting;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/script")
public class ScriptController {
    private final UserScriptService userScriptService;
    private final ScriptExecutionService scriptExecutionService;

    public ScriptController(UserScriptService userScriptService, ScriptExecutionService scriptExecutionService) {
        this.userScriptService = userScriptService;
        this.scriptExecutionService = scriptExecutionService;
    }

    @PostMapping
    public UserScript createScript(@RequestBody UserScript script) {
        return userScriptService.saveScript(script);
    }

    @GetMapping
    public List<UserScript> getUserScripts(@RequestParam String author) {
        return userScriptService.getScriptsByAuthor(author);
    }

    @PostMapping("/{id}/execute")
    public Object executeScript(@PathVariable UUID id) throws ScriptException {
        UserScript script = userScriptService.getScriptById(id);
        return scriptExecutionService.executeScript(script);
    }

    @DeleteMapping("/{id}")
    public void deleteScript(@PathVariable UUID id) {
        userScriptService.deleteScript(id);
    }
}
