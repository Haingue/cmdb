package com.management.cmdb.backend.scripting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserScriptRepository extends JpaRepository<UserScript, UUID> {
    List<UserScript> findByAuthor(String author);
}
