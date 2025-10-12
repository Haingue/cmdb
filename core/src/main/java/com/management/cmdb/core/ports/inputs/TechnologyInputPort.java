package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.technology.Technology;

public interface TechnologyInputPort {

    Technology create (Technology technology);
    Technology update (Technology technology);
    void archive (String name);
    void delete (String name);

    Technology findByName (String name);

}
