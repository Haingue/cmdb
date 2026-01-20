package com.management.cmdb.core.ports.inputs.technical;

import com.management.cmdb.core.models.business.identity.User;

import java.io.Serializable;

public interface CrudOperation <Entity, Identifier extends Serializable> {
    Entity findOne (Identifier identifier, User initiator);
    Entity create (Entity newEntity, User initiator);
    Entity update (Entity environment, User initiator);
    void archive (Identifier uuid, User initiator);
    void delete (Identifier uuid, User initiator);
}
