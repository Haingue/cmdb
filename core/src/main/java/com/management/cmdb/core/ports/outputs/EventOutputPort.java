package com.management.cmdb.core.ports.outputs;

import com.management.cmdb.core.models.business.Event;

import java.util.List;

public interface EventOutputPort {

    boolean notify(Event event);
    boolean notify(Event event, List<String> recipients);

}
