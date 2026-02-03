package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.technical.ComponentVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Software extends Component implements Serializable {

    private Host host;

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.accept(this);
    }

    public Host getHost() {
        return host;
    }

    @Override
    public boolean needsUpdate() {
        return super.needsUpdate() || host.needsUpdate();
    }

    @Override
    public Software updateFrom(Component source) {
        super.updateFrom(source);
        if (source instanceof Software softwareSource) {
            this.host = softwareSource.host; // TODO clone object ?
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Software software)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(host, software.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), host);
    }

    @Override
    public String toString() {
        return "Software{" +
                "name=" + getName() +
                ", host=" + host +
                '}';
    }
}
