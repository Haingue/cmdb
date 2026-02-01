package com.management.cmdb.core.models.technical;

import com.management.cmdb.core.models.business.component.*;

public interface ComponentVisitor<T> {
    T accept(Component component);
    T accept(Host host);
    T accept(Hardware hardware);
    T accept(Software software);
    T accept(VirtualMachine vm);
}