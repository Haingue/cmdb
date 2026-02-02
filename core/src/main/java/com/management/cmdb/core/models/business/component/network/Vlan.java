package com.management.cmdb.core.models.business.component.network;

import com.management.cmdb.core.models.business.component.Host;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vlan {

    String number;
    String description;
    Host firewall;
    String ipRange; // TODO should use something else

}
