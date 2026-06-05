package com.management.cmdb.core.ports.inputs;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.ports.inputs.technical.CrudOperation;

import java.util.List;

public interface TechnologyInputPort extends CrudOperation<Technology, String> {

    Technology create (Technology technology);
    Technology update (Technology technology);
    void archive (String name);
    void delete (String name);

    Technology findByName (String name);

    List<Component> setMinimalVersionAndScanAssets (String name, Version newMinimalVersion);

}
