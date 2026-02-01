package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.technical.ComponentVisitor;
import com.management.cmdb.core.ports.inputs.technical.CrudOperation;

import java.util.UUID;

public interface ComponentInputPort extends CrudOperation<Component, UUID> {


}
