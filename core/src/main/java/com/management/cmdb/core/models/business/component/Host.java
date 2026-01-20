package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.business.constant.ActiveDirectoryDomainName;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.NetworkArea;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Host extends Component {

    private String dns;

    private ActiveDirectoryDomainName domain;
    private NetworkArea networkArea;

    private String macAddress;
    private InetAddress ipAddress;
    private String vlan;

    private DayOfWeek patchingDay;

    private Set<Host> communicatesWith;

    public Host(String name, String description, ComponentType type, Version version, String certificate, Technology operatingSystem,
                String dns, String macAddress, InetAddress ipAddress, String vlan, DayOfWeek patchingDay, ActiveDirectoryDomainName domain, NetworkArea networkArea) {
        super(name, description, type, version, certificate, operatingSystem);
        this.dns = dns;
        this.domain = domain;
        this.networkArea = networkArea;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.vlan = vlan;
        this.patchingDay = patchingDay;
        this.communicatesWith = new HashSet<>();
    }

    public Host(Component source, String dns, String macAddress, InetAddress ipAddress, String vlan, DayOfWeek patchingDay, ActiveDirectoryDomainName domain, NetworkArea networkArea) {
        super(source, source.getName(), source.getDescription(), source.getType(), source.getVersion(), source.getCertificate(), source.getTechnology());
    }

    @Override
    public Host updateFrom(Component source) {
        super.updateFrom(source);
        if (source instanceof Host hostSource) {
            this.dns = hostSource.dns;
            this.vlan = hostSource.vlan;
            this.ipAddress = hostSource.ipAddress;
            this.patchingDay = hostSource.patchingDay;
        }
        return this;
    }

    @Override
    public String toString() {
        return "Host{" +
                "dns='" + dns + '\'' +
                ", networkArea=" + networkArea +
                ", domain=" + domain +
                ", ipAddress=" + ipAddress +
                ", vlan='" + vlan + '\'' +
                '}';
    }
}
