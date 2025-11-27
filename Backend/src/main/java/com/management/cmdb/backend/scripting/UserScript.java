package com.management.cmdb.backend.scripting;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class UserScript {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ScriptLanguage language;

    @Lob
    private String content;

    private String author;
}
