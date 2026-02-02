package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.technical.ComponentVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Hardware extends Host {

    private String location;

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.accept(this);
    }

    public String getLocation() {
        return location;
    }

    @Override
    public Hardware updateFrom(Component source) {
        super.updateFrom(source);
        if (source instanceof Hardware hardwareSource) {
            this.location = hardwareSource.location;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Hardware hardware)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(location, hardware.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), location);
    }

    @Override
    public String toString() {
        return "Software{" +
                "name=" + getName() +
                ", location=" + location +
                ", dns='" + getDns() + '\'' +
                ", networkArea=" + getNetworkArea() +
                ", domain=" + getDomain() +
                ", ipAddress=" + getIpAddress() +
                ", vlan='" + getVlan() + '\'' +
                '}';
    }
}
