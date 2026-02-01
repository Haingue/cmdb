package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import com.management.cmdb.core.models.technical.Event;
import com.management.cmdb.core.models.technical.VersionedSavedEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
public class Component extends VersionedSavedEntity {

    private String name;
    private String description;
    private ComponentType type;
    private String certificate;
    private Technology technology;
    private Version version;

    public Component(String name, String description, ComponentType type, Version version, String certificate, Technology technology) {
        super();
        this.name = name;
        this.description = description;
        this.type = Objects.requireNonNullElse(type, ComponentType.UNKNOWN);
        this.version = version;
        this.certificate = certificate;
        this.technology = technology;
    }

    public Component (UUID uuid, long revision, List<Event> events, LocalDateTime creationDatetime, LocalDateTime archiveDatetime,
                       String name, String description, ComponentType type, Version version, String certificate, Technology technology)
    {
        super(VersionedSavedEntity.reload(uuid, revision, events, creationDatetime, archiveDatetime));
        this.name = name;
        this.description = description;
        this.type = Objects.requireNonNullElse(type, ComponentType.UNKNOWN);
        this.version = version;
        this.certificate = certificate;
        this.technology = technology;
    }

    public Component (VersionedSavedEntity source, String name, String description, ComponentType type, Version version, String certificate, Technology technology) {
        super(source);
        this.name = name;
        this.description = description;
        this.type = Objects.requireNonNullElse(type, ComponentType.UNKNOWN);
        this.version = version;
        this.certificate = certificate;
        this.technology = technology;
    }

    public Component (Component source) {
        this(source, source.getName(), source.getDescription(), source.getType(), source.getVersion(), source.getCertificate(), source.getTechnology());
    }

    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.accept(this);
    }

    public boolean needsUpdate()
    {
        return technology.needsUpdate(version);
    }

    public void checkIntegrity () {
        if(StringUtils.isBlank(name)) throw new InvalidObjectException("name cannot be blank", this);
        if(type == null) throw new InvalidObjectException("type cannot be null", this);
        if(version == null) throw new InvalidObjectException("version cannot be null", this);
        if(technology == null) throw new InvalidObjectException("technology cannot be null", this);
    }

    public Component updateFrom(Component component) {
        this.description = component.getDescription();
        this.type = Objects.requireNonNullElse(type, ComponentType.UNKNOWN);
        this.version = Objects.requireNonNullElse(component.version, this.version);
        this.certificate = Objects.requireNonNullElse(component.certificate, this.certificate);
        this.technology = Objects.requireNonNullElse(component.technology, this.technology);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Component component)) return false;
        return super.equals(component) && Objects.equals(name, component.name) && type == component.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, type);
    }

    @Override
    public String toString() {
        return "Component{" +
                "uuid=" + getUuid() +
                ", version=" + version +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }

}
