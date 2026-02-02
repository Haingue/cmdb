package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.technical.ComponentVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class GenericComponent extends Component {

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.accept(this);
    }


}
