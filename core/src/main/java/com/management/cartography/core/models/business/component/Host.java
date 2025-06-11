package com.management.cartography.core.models.business.component;

import com.management.cartography.core.models.business.constants.ComponentType;
import com.management.cartography.core.models.business.technology.Technology;
import com.management.cartography.core.models.business.technology.Version;

import java.net.InetAddress;
import java.time.DayOfWeek;
import java.util.UUID;

public class Host extends Component {

    private String hostname;
    private String dns;

    private String macAddress;
    private InetAddress ipAddress;
    private String vlan;

    private DayOfWeek patchingDay;

    public Host(UUID uuid, String name, String description, ComponentType type, Version version, String certificate, Technology operatingSystem, String hostname, String dns, String macAddress, InetAddress ipAddress, String vlan, DayOfWeek patchingDay) {
        super(uuid, name, description, type, version, certificate, operatingSystem);
        this.hostname = hostname;
        this.dns = dns;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.vlan = vlan;
        this.patchingDay = patchingDay;
    }

    public String getHostname() {
        return hostname;
    }

    public String getDns() {
        return dns;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public String getVlan() {
        return vlan;
    }

    public DayOfWeek getPatchingDay() {
        return patchingDay;
    }

    public Technology getOperatingSystem() {
        return super.getTechnology();
    }
}
