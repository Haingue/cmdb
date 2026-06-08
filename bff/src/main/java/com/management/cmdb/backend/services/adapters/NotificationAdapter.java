package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.core.models.business.Notification;
import com.management.cmdb.core.ports.outputs.NotificationOutputPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationAdapter implements NotificationOutputPort {

    @Override
    public boolean notify(Notification notification) {
        return false;
    }

    @Override
    public boolean notify(Notification notification, List<String> list) {
        return false;
    }
}
