package com.management.cmdb.core.service;

import com.management.cmdb.core.models.business.Event;
import com.management.cmdb.core.models.business.EventType;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.inputs.BusinessServiceInputPort;
import com.management.cmdb.core.ports.outputs.BusinessServiceOutputPort;
import com.management.cmdb.core.ports.outputs.EventOutputPort;

import java.time.Instant;

public class BusinessServiceService implements BusinessServiceInputPort {

    private final BusinessServiceOutputPort businessServiceOutputPort;
    private final EventOutputPort eventOutputPort;

    public BusinessServiceService(BusinessServiceOutputPort businessServiceOutputPort, EventOutputPort eventOutputPort) {
        this.businessServiceOutputPort = businessServiceOutputPort;
        this.eventOutputPort = eventOutputPort;
    }

    private void isUniqueAbbreviation(String abbreviation) {
        businessServiceOutputPort.findByAbbreviation(abbreviation)
                .ifPresent(existingBusinessService -> {
                    throw new InvalidObjectException("Abbreviation " + abbreviation + " already used by " + existingBusinessService.getName());
                });
    }

    @Override
    public BusinessService findOne(String name, User initiator) {
        // TODO check initiator permissions
        return businessServiceOutputPort.findOne(name)
                .orElseThrow(() -> new NotFoundException(name));
    }

    @Override
    public BusinessService create(BusinessService businessService, User initiator) {
        // TODO check initiator permissions
        if (businessService == null) throw new InvalidObjectException("Business service cannot be null");
        businessService.checkIntegrity();

        isUniqueAbbreviation(businessService.getAbbreviation());

        BusinessService savedBusinessService = businessServiceOutputPort.save(businessService);
        eventOutputPort.notify(
                Event.builder()
                        .title(savedBusinessService.getName())
                        .type(EventType.ITEM_CREATED)
                        .payload(savedBusinessService)
                        .source(initiator.toString())
                        .timestamp(Instant.now())
                        .build());
        return savedBusinessService;
    }

    @Override
    public BusinessService update(BusinessService businessService, User initiator) {
        // TODO check initiator permissions
        if (businessService == null) throw new InvalidObjectException("Business service cannot be null");
        businessService.checkIntegrity();

        BusinessService previousValue = businessServiceOutputPort.findOne(businessService.getName())
                .orElseThrow(() -> new NotFoundException(businessService.getName()));
        isUniqueAbbreviation(businessService.getAbbreviation());
        // previousValue.setName(businessService.getName());
        previousValue.setAbbreviation(businessService.getAbbreviation());

        BusinessService savedBusinessService = businessServiceOutputPort.save(previousValue);
        eventOutputPort.notify(
                Event.builder()
                        .title(savedBusinessService.getName())
                        .type(EventType.ITEM_UPDATED)
                        .payload(savedBusinessService)
                        .source(initiator.toString())
                        .timestamp(Instant.now())
                        .build());
        return savedBusinessService;
    }

    @Override
    public void archive(String name, User initiator) {
        // TODO check initiator permissions
        throw new NotImplemented();
    }

    @Override
    public void delete(String name, User initiator) {
        // TODO check initiator permissions
        if (name == null) throw new CoreException("BusinessService name cannot be null");
        BusinessService existingBusinessService = businessServiceOutputPort.findOne(name)
                .orElseThrow(() -> new NotFoundException(name));
        businessServiceOutputPort.delete(existingBusinessService.getName());

        eventOutputPort.notify(
                Event.builder()
                        .title(existingBusinessService.getName())
                        .type(EventType.ITEM_DELETED)
                        .payload(existingBusinessService)
                        .source(initiator.toString())
                        .timestamp(Instant.now())
                        .build());
    }
}
