package com.management.cmdb.core.models.business.component;

public class VirtualMachine extends Host {

    private Host esx;

    public VirtualMachine(Host source, Host esx) {
        super(source, source.getDns(), source.getMacAddress(), source.getIpAddress(), source.getVlan(), source.getPatchingDay(), source.getDomain(), source.getNetworkArea());
        this.esx = esx;
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
}
