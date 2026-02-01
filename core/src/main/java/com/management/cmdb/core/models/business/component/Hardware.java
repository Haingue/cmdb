package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.technical.ComponentVisitor;

public class Hardware extends Host {

    private String location;

    public Hardware(Host source, String location) {
        super(source, source.getDns(), source.getMacAddress(), source.getIpAddress(), source.getVlan(), source.getPatchingDay(), source.getDomain(), source.getNetworkArea());
        this.location = location;
    }

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
