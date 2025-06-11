package com.management.cartography.core.models.business.component;

import com.management.cartography.core.models.business.constants.ComponentType;
import com.management.cartography.core.models.business.technology.Technology;
import com.management.cartography.core.models.business.technology.Version;

import java.net.InetAddress;
import java.time.DayOfWeek;
import java.util.UUID;

public class Hardware extends Host {

    private String location;

    public Hardware(UUID uuid, String name, String description, Version version, String certificate, Technology operatingSystem, String hostname, String dns, String macAddress, InetAddress ipAddress, String vlan, DayOfWeek patchingDay, String location) {
        super(uuid, name, description, ComponentType.HARDWARE, version, certificate, operatingSystem, hostname, dns, macAddress, ipAddress, vlan, patchingDay);
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
