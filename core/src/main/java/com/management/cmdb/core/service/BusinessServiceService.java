package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.inputs.BusinessServiceInputPort;
import com.management.cmdb.core.ports.outputs.BusinessServiceOutputPort;

public class BusinessServiceService implements BusinessServiceInputPort {

    private final BusinessServiceOutputPort businessServiceOutputPort;

    public BusinessServiceService(BusinessServiceOutputPort businessServiceOutputPort) {
        this.businessServiceOutputPort = businessServiceOutputPort;
    }

    private void isUniqueAbbreviation(String abbreviation) {
        businessServiceOutputPort.findByAbbreviation(abbreviation)
                .ifPresent(existingBusinessService -> {
                    throw new InvalidObjectException("Abbreviation " + abbreviation + " already used by " + existingBusinessService.name());
                });
    }

    @Override
    public BusinessService create(BusinessService businessService) {
        if (businessService == null) throw new InvalidObjectException("Business service cannot be null");
        businessService.isValid();

        isUniqueAbbreviation(businessService.abbreviation());

        return businessServiceOutputPort.save(businessService);
    }

    @Override
    public BusinessService update(BusinessService businessService) {
        if (businessService == null) throw new InvalidObjectException("Business service cannot be null");
        businessService.isValid();

        businessServiceOutputPort.findByAbbreviation(businessService.abbreviation())
                .orElseThrow(() -> new NotFoundException(businessService.name()));
        isUniqueAbbreviation(businessService.abbreviation());

        return businessServiceOutputPort.save(businessService);
    }

    @Override
    public void archive(String name) {
        throw new NotImplemented();
    }

    @Override
    public void delete(String name) {
        if (name == null) throw new CoreException("BusinessService name cannot be null");
        businessServiceOutputPort.findOne(name)
                .orElseThrow(() -> new NotFoundException(name));
        businessServiceOutputPort.delete(name);
    }

    @Override
    public BusinessService findOneByName(String name) {
        return businessServiceOutputPort.findOne(name)
                .orElseThrow(() -> new NotFoundException(name));
    }
}
