package com.management.cmdb.core.models.business;

import com.management.cmdb.core.models.technical.UniqueEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
public class Event extends UniqueEntity {

    protected String title;
    protected Object payload;
    protected EventType type;
    protected Instant timestamp;
    protected String source;


}
