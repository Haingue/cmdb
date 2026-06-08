package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.Notification;

import java.util.List;

public interface NotificationOutputPort {

    boolean notify(Notification notification);
    boolean notify(Notification notification, List<String> recipients);

}
