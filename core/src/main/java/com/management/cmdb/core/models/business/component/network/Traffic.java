package com.management.cmdb.core.models.business.component.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Traffic implements Serializable {

    private InetAddress sourceIp;
    private InetAddress destinationIp;
    private int destinationPort;
    private String protocol;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Traffic traffic)) return false;
        return destinationPort == traffic.destinationPort && Objects.equals(sourceIp, traffic.sourceIp) && Objects.equals(destinationIp, traffic.destinationIp) && Objects.equals(protocol, traffic.protocol) && Objects.equals(description, traffic.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIp, destinationIp, destinationPort, protocol, description);
    }

    @Override
    public String toString() {
        return "Traffic{" +
                "description='" + description + '\'' +
                ", protocol='" + protocol + '\'' +
                ", destinationPort=" + destinationPort +
                ", destinationIp=" + destinationIp +
                ", sourceIp=" + sourceIp +
                '}';
    }
}
