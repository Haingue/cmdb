package com.management.cmdb.backend.scripting;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserScriptService {

    private final UserScriptRepository scriptRepository;

    public UserScriptService(UserScriptRepository scriptRepository) {
        this.scriptRepository = scriptRepository;
    }

    public UserScript saveScript(UserScript script) {
        return scriptRepository.save(script);
    }

    public List<UserScript> getScriptsByAuthor(String author) {
        return scriptRepository.findByAuthor(author);
    }

    public UserScript getScriptById(UUID id) {
        return scriptRepository.findById(id).orElseThrow();
    }

    public void deleteScript(UUID id) {
        scriptRepository.deleteById(id);
    }
}
