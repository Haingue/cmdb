package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.business.constants.ComponentType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Component {

    private UUID uuid;
    private String name;
    private String description;
    private ComponentType type;
    private Version version;
    private String certificate;
    private Technology technology;

    private LocalDateTime creationTimeStamp;

    public Component(UUID uuid, String name, String description, ComponentType type, Version version, String certificate, Technology technology) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.type = Objects.requireNonNullElse(type, ComponentType.UNKNOWN);
        this.version = version;
        this.certificate = certificate;
        this.technology = technology;
        this.creationTimeStamp = LocalDateTime.now();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ComponentType getType() {
        return type;
    }

    public Version getVersion() {
        return version;
    }

    public Technology getTechnology() {
        return technology;
    }

    public LocalDateTime getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public boolean needsUpdate()
    {
        return technology.needsUpdate(version);
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @Override
    public String toString() {
        return "Component{" +
                "type=" + type +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", version=" + version +
                '}';
    }
}
