package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.constant.ActiveDirectoryDomainName;
import com.management.cmdb.core.models.business.constant.NetworkArea;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Host extends Component implements Serializable {

    private String dns;

    private ActiveDirectoryDomainName domain;
    private NetworkArea networkArea;

    private String macAddress;
    private InetAddress ipAddress;
    private Vlan vlan;

    private DayOfWeek patchingDay;

    @Builder.Default
    private Set<Host> communicatesWith = new HashSet<>();

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.accept(this);
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
    public boolean equals(Object o) {
        if (!(o instanceof Host host)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(ipAddress, host.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ipAddress);
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
