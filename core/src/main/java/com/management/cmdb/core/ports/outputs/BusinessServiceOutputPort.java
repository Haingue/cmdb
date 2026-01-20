package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.project.BusinessService;

import java.util.Optional;

public interface BusinessServiceOutputPort {
    Optional<BusinessService> findOne(String name);
    Optional<BusinessService> findByAbbreviation(String abbreviation);

    BusinessService save(BusinessService businessService);

    void delete(String name);
}
