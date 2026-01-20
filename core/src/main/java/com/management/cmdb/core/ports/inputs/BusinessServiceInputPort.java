package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.ports.inputs.technical.CrudOperation;

public interface BusinessServiceInputPort extends CrudOperation<BusinessService, String> {

}
