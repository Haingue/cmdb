package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.business.constant.TechnologyType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Software extends Component implements Serializable {

    private TechnologyType softwareType;
    private Set<Host> hosts;
    private URI repositoryUrl;

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.accept(this);
    }

    @Override
    public boolean needsUpdate() {
        return super.needsUpdate();
    }

    @Override
    public Software updateFrom(Component source) {
        super.updateFrom(source);
        if (source instanceof Software softwareSource) {
            this.softwareType = softwareSource.softwareType;
            this.hosts = softwareSource.hosts; // TODO clone object ?
            this.repositoryUrl = softwareSource.repositoryUrl;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Software software)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(super.getName(), software.getName())
                && Objects.equals(softwareType, software.softwareType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), softwareType, repositoryUrl);
    }

    @Override
    public String toString() {
        return "Software{" +
                "name=" + getName() +
                ", softwareType=" + softwareType +
                ", technology=" + getTechnology() +
                '}';
    }
}
