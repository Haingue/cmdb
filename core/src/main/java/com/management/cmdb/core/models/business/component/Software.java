package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.technical.Event;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Software extends Component {

    private Host host;

    public Software(@NonNull UUID uuid, long revision, List<Event> events, LocalDateTime creationDatetime, LocalDateTime archiveDatetime,
                    String name, String description, ComponentType type, Version version, String certificate, Technology technology,
                    Host host) {
        super(uuid, revision, events, creationDatetime, archiveDatetime, name, description, type, version, certificate, technology);
        this.host = host;
    }
    public Software(Component source, Host host) {
        super(source);
        this.host = host;
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
    public String toString() {
        return "Software{" +
                "name=" + getName() +
                ", host=" + host +
                '}';
    }
}
