package com.management.cmdb.core.fake;

import com.management.cmdb.core.models.business.project.BusinessService;

public enum FakeBusinessService {

    BUSINESS_SERVICE_1(new BusinessService(null, "Business service 1", "BS_1", 0L, null, null, null));

    public final BusinessService businessService;

    FakeBusinessService(BusinessService businessService) {
        this.businessService = businessService;
    }

}
