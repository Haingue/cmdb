package com.management.cmdb.core.models.business.component;

import com.management.cmdb.core.models.technical.ComponentVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VirtualMachine extends Host {

    private Host esx;

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.accept(this);
    }

    public Host getEsx() {
        return esx;
    }

    @Override
    public VirtualMachine updateFrom(Component source) {
        super.updateFrom(source);
        if (source instanceof VirtualMachine virtualMachineSource) {
            this.esx = virtualMachineSource.esx;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VirtualMachine that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(esx, that.esx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), esx);
    }

    @Override
    public String toString() {
        return "VirtualMachine{" +
                "name=" + getName() +
                ", esx=" + esx +
                '}';
    }
}
