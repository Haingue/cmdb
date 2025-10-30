package com.management.cmdb.core.fake;

import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Project;

public enum FakeBusinessService {

    BUSINESS_SERVICE_1(new BusinessService("Business service 1", "BS_1"));

    public final BusinessService businessService;

    FakeBusinessService(BusinessService businessService) {
        this.businessService = businessService;
    }

}
