import type { ItemDto } from "../service/inventory/types"

export default class Host {
    uuid?: string;
    hostname?: string;
    vlan?: string;
    operatingSystem?: string;
    creationTimeStamp?: string;
    type?: string;
    version?: string;
    networkArea?: string;
    technology?: string;
    macAddress?: string;
    certificate?: string;
    ipAddress?: string;
    patchingDay?: string;
    domain?: string;
    dns?: string;
    communicatesWith?: string;

    constructor (item: ItemDto) {
        if (!item.attributes) return
        for (const attibute of item.attributes) {
            switch (attibute.label) {
                case 'Hostname':
                    this.hostname = attibute.value as string
                    break
                case 'Uuid':
                    this.uuid = attibute.value as string
                    break
                case 'Vlan':
                    this.vlan = attibute.value as string
                    break
                case 'OperatingSystem':
                    this.operatingSystem = attibute.value as string
                    break
                case 'CreationTimeStamp':
                    this.creationTimeStamp = attibute.value as string
                    break
                case 'Type':
                    this.type = attibute.value as string
                    break
                case 'Version':
                    this.version = attibute.value as string
                    break
                case 'NetworkArea':
                    this.networkArea = attibute.value as string
                    break
                case 'Technology':
                    this.technology = attibute.value as string
                    break
                case 'MacAddress':
                    this.macAddress = attibute.value as string
                    break
                case 'Certificate':
                    this.certificate = attibute.value as string
                    break
                case 'IpAddress':
                    this.ipAddress = attibute.value as string
                    break
                case 'PatchingDay':
                    this.patchingDay = attibute.value as string
                    break
                case 'Domain':
                    this.domain = attibute.value as string
                    break
                case 'Dns':
                    this.dns = attibute.value as string
                    break
                case 'CommunicatesWith':
                    this.communicatesWith = attibute.value as string
                    break
            }
        }
    }
}