package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.component.Component;

import java.util.UUID;

public interface ComponentInputPort {

    Component create (Component component);
    Component update (Component component);
    void archive (UUID uuid);
    void delete (UUID uuid);

    Component findOneByUuid (UUID uuid);

}
