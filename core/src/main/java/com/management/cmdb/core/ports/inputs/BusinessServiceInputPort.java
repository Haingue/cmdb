package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.project.BusinessService;

public interface BusinessServiceInputPort {

    BusinessService create (BusinessService businessService);
    BusinessService update (BusinessService businessService);
    void archive (String name);
    void delete (String name);

    BusinessService findOneByName (BusinessService businessService);

}
