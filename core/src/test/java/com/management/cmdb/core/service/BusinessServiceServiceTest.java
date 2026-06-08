package com.management.cmdb.core.service;

import com.management.cmdb.core.fake.FakeBusinessService;
import com.management.cmdb.core.fake.FakeUser;
import com.management.cmdb.core.models.business.Event;
import com.management.cmdb.core.models.business.EventType;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
import com.management.cmdb.core.models.exceptions.NotFoundException;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.ports.outputs.BusinessServiceOutputPort;
import com.management.cmdb.core.ports.outputs.EventOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.argThat;

@ExtendWith(MockitoExtension.class)
class BusinessServiceServiceTest {

    @Mock
    private BusinessServiceOutputPort businessServiceOutputPort;

    @Mock
    private EventOutputPort eventOutputPort;

    @InjectMocks
    private BusinessServiceService businessServiceService;

    private final BusinessService validBusinessService = FakeBusinessService.BUSINESS_SERVICE_1.businessService;
    private final BusinessService anotherBusinessService = new BusinessService("Another Service", "ANS");


    @Test
    void findOne_shouldReturnBusinessService_whenFound() {
        given(businessServiceOutputPort.findOne(validBusinessService.getName()))
                .willReturn(Optional.of(validBusinessService));

        BusinessService result = businessServiceService.findOne(validBusinessService.getName(), FakeUser.SUPER_ADMIN.user);

        assertEquals(validBusinessService, result);
    }

    @Test
    void findOne_shouldThrowNotFoundException_whenNotFound() {
        String name = "NonExistentService";
        given(businessServiceOutputPort.findOne(name))
                .willReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> businessServiceService.findOne(name, FakeUser.SUPER_ADMIN.user));
    }


    @Test
    void create_shouldSaveAndReturnBusinessService_whenValid() {
        // Arrange
        given(businessServiceOutputPort.findByAbbreviation(validBusinessService.getAbbreviation()))
                .willReturn(Optional.empty());
        given(businessServiceOutputPort.save(validBusinessService))
                .willReturn(validBusinessService);
        given(eventOutputPort.notify(any(Event.class)))
                .willReturn(true);

        // Act
        BusinessService result = businessServiceService.create(validBusinessService, FakeUser.SUPER_ADMIN.user);

        // Assert
        assertEquals(validBusinessService, result);
        then(eventOutputPort).should().notify(argThat(notification ->
                notification.getType() == EventType.ITEM_CREATED
                        && notification.getSource().equals(FakeUser.SUPER_ADMIN.user.toString())
                        && notification.getTitle().equals(validBusinessService.getName())
                        && notification.getPayload().equals(validBusinessService)
                        && notification.getTimestamp() != null));
    }

    @Test
    void create_shouldThrowInvalidObjectException_whenBusinessServiceIsNull() {
        assertThrows(InvalidObjectException.class,
                () -> businessServiceService.create(null, FakeUser.SUPER_ADMIN.user));
    }

    @Test
    void create_shouldThrowInvalidObjectException_whenAbbreviationAlreadyExists() {
        given(businessServiceOutputPort.findByAbbreviation(validBusinessService.getAbbreviation()))
                .willReturn(Optional.of(anotherBusinessService));

        assertThrows(InvalidObjectException.class,
                () -> businessServiceService.create(validBusinessService, FakeUser.SUPER_ADMIN.user));
    }

    @Test
    void create_shouldThrowInvalidObjectException_whenIntegrityCheckFails() {
        BusinessService invalid = new BusinessService("", "");
        assertThrows(InvalidObjectException.class,
                () -> businessServiceService.create(invalid, FakeUser.SUPER_ADMIN.user));
    }


    @Test
    void update_shouldSaveUpdateAndNotify_whenValid() {
        BusinessService existing = new BusinessService(validBusinessService.getName(), "OLD");
        given(businessServiceOutputPort.findOne(validBusinessService.getName()))
                .willReturn(Optional.of(existing));
        given(businessServiceOutputPort.findByAbbreviation(validBusinessService.getAbbreviation()))
                .willReturn(Optional.empty());
        given(businessServiceOutputPort.save(any(BusinessService.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(eventOutputPort.notify(any(Event.class)))
                .willReturn(true);

        BusinessService result = businessServiceService.update(validBusinessService, FakeUser.BUSINESS_MEMBER.user);

        assertEquals(validBusinessService.getAbbreviation(), result.getAbbreviation());
        then(eventOutputPort).should().notify(any(Event.class));
    }

    @Test
    void update_shouldThrowInvalidObjectException_whenBusinessServiceIsNull() {
        assertThrows(InvalidObjectException.class,
                () -> businessServiceService.update(null, FakeUser.SUPER_ADMIN.user));
    }

    @Test
    void update_shouldThrowNotFoundException_whenBusinessServiceNotFound() {
        given(businessServiceOutputPort.findOne(validBusinessService.getName()))
                .willReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> businessServiceService.update(validBusinessService, FakeUser.SUPER_ADMIN.user));
    }

    @Test
    void update_shouldThrowInvalidObjectException_whenIntegrityCheckFails() {
        BusinessService invalid = new BusinessService(validBusinessService.getName(), "");
        assertThrows(InvalidObjectException.class,
                () -> businessServiceService.update(invalid, FakeUser.SUPER_ADMIN.user));
    }

    @Test
    void update_shouldThrowInvalidObjectException_whenAbbreviationAlreadyUsed() {
        given(businessServiceOutputPort.findOne(validBusinessService.getName()))
                .willReturn(Optional.of(validBusinessService));
        given(businessServiceOutputPort.findByAbbreviation(anotherBusinessService.getAbbreviation()))
                .willReturn(Optional.of(anotherBusinessService));

        BusinessService updateWithDuplicateAbbreviation = new BusinessService(
                validBusinessService.getName(), anotherBusinessService.getAbbreviation());

        assertThrows(InvalidObjectException.class,
                () -> businessServiceService.update(updateWithDuplicateAbbreviation, FakeUser.SUPER_ADMIN.user));
    }

    @Test
    void update_shouldSendNotificationWithCorrectPayload() {
        BusinessService existing = new BusinessService(validBusinessService.getName(), "OLD");
        given(businessServiceOutputPort.findOne(validBusinessService.getName()))
                .willReturn(Optional.of(existing));
        given(businessServiceOutputPort.findByAbbreviation(validBusinessService.getAbbreviation()))
                .willReturn(Optional.empty());
        given(businessServiceOutputPort.save(any(BusinessService.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(eventOutputPort.notify(any(Event.class)))
                .willReturn(true);

        businessServiceService.update(validBusinessService, FakeUser.BUSINESS_MEMBER.user);

        then(eventOutputPort).should().notify(argThat(notification ->
                notification.getType() == EventType.ITEM_UPDATED
                        && notification.getSource().equals(FakeUser.BUSINESS_MEMBER.user.toString())
                        && notification.getTitle().equals(validBusinessService.getName())
                        && notification.getPayload().equals(validBusinessService)
                        && notification.getTimestamp() != null));
    }

    @Test
    void archive_shouldThrowNotImplemented() {
        assertThrows(NotImplemented.class,
                () -> businessServiceService.archive(validBusinessService.getName(), FakeUser.SUPER_ADMIN.user));
    }

    @Test
    void delete_shouldDeleteBusinessService_whenFound() {
        given(businessServiceOutputPort.findOne(validBusinessService.getName()))
                .willReturn(Optional.of(validBusinessService));
        given(eventOutputPort.notify(any(Event.class)))
                .willReturn(true);

        assertDoesNotThrow(() ->
                businessServiceService.delete(validBusinessService.getName(), FakeUser.SUPER_ADMIN.user));

        then(businessServiceOutputPort).should().delete(validBusinessService.getName());
        then(eventOutputPort).should().notify(argThat(notification ->
                notification.getType() == EventType.ITEM_DELETED
                        && notification.getSource().equals(FakeUser.SUPER_ADMIN.user.toString())
                        && notification.getTitle().equals(validBusinessService.getName())
                        && notification.getPayload().equals(validBusinessService)
                        && notification.getTimestamp() != null));
    }

    @Test
    void delete_shouldThrowCoreException_whenNameIsNull() {
        assertThrows(CoreException.class,
                () -> businessServiceService.delete(null, FakeUser.SUPER_ADMIN.user));
    }

    @Test
    void delete_shouldThrowNotFoundException_whenBusinessServiceNotFound() {
        String name = "NonExistentService";
        given(businessServiceOutputPort.findOne(name))
                .willReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> businessServiceService.delete(name, FakeUser.SUPER_ADMIN.user));
    }

    @Test
    void create_shouldCheckAbbreviationUniqueness() {
        given(businessServiceOutputPort.findByAbbreviation(validBusinessService.getAbbreviation()))
                .willReturn(Optional.empty());
        given(businessServiceOutputPort.save(validBusinessService))
                .willReturn(validBusinessService);

        businessServiceService.create(validBusinessService, FakeUser.SUPER_ADMIN.user);

        then(businessServiceOutputPort).should().findByAbbreviation(validBusinessService.getAbbreviation());
    }
}
