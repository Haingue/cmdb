package com.management.cmdb.backend.services.adapters;

import com.management.cmdb.core.models.business.Event;
import com.management.cmdb.core.ports.outputs.EventOutputPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventAdapter implements EventOutputPort {

    @Override
    public boolean notify(Event notification) {
        return false;
    }

    @Override
    public boolean notify(Event notification, List<String> list) {
        return false;
    }
}
