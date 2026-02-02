package com.management.cmdb.core.models.business.component.network;

import com.management.cmdb.core.models.business.component.Host;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Vlan {

    private int number;
    private String description;
    private Host firewall;
    private String ipRange;

}
