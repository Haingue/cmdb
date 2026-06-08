package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.technical.Checkable;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import com.management.cmdb.core.models.technical.VersionedSavedEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Component extends VersionedSavedEntity implements Serializable, Checkable {

    private String name;
    private String description;
    private ComponentType type;
    private String certificate;
    private Technology technology;
    private Version version;

    public abstract <T> T accept(ComponentVisitor<T> visitor);

    public boolean needsUpdate()
    {
        return technology.needsUpdate(version);
    }

    @Override
    public void checkIntegrity () throws InvalidObjectException {
        super.checkIntegrity();
        if(StringUtils.isBlank(name)) throw new InvalidObjectException("name cannot be blank", this);
        if(type == null) throw new InvalidObjectException("type cannot be null", this);
        if(version == null) throw new InvalidObjectException("version cannot be null", this);
        if(technology == null) throw new InvalidObjectException("technology cannot be null", this);
    }

    public Component updateFrom(Component component) {
        this.description = component.getDescription();
        this.type = Objects.requireNonNullElse(type, ComponentType.HOST);
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
