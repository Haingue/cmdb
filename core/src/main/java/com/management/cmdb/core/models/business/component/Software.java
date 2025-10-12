package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.business.constants.ComponentType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;

import java.util.UUID;

public class Software extends Component {

    private final Host host;

    public Software(UUID uuid, String name, String description, Version version, String certificate, Technology technology, Host host) {
        super(uuid, name, description, ComponentType.SOFTWARE, version, certificate, technology);
        this.host = host;
    }

    public Host getHost() {
        return host;
    }

    @Override
    public boolean needsUpdate() {
        return super.needsUpdate() || host.needsUpdate();
    }
}
