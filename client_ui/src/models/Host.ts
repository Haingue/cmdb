import { DomainLabel, ItemTypeLabel, LinkTypeLabel, NetworkAreaLabel, PatchingDayLabel } from "../service/backend/constants";
import type { Component, Host } from "../service/backend/types";
import type { DateTime, ItemDto, LinkDto, UUID } from "../service/inventory/types"

export default class HostModel {
  uuid?: UUID;
  name?: string;
  description?: string;
  vlan?: string;
  operatingSystem?: string;
  type?: (typeof ItemTypeLabel)[keyof typeof ItemTypeLabel];
  version?: string;
  networkArea?: (typeof NetworkAreaLabel)[keyof typeof NetworkAreaLabel];
  technology?: string;
  macAddress?: string;
  certificate?: string;
  ipAddress?: string;
  patchingDay?: (typeof PatchingDayLabel)[keyof typeof PatchingDayLabel];
  domain?: (typeof DomainLabel)[keyof typeof DomainLabel];
  dns?: string;
  communicatesWith?: LinkDto[];
  environmentUuid?: UUID;
  creationTimeStamp?: DateTime;

  constructor (item: ItemDto) {
    if (!item.attributes) return
    this.uuid = item.uuid
    this.name = item.name
    this.description = item.description
    this.creationTimeStamp = item.createdDate
    for (const attibute of item.attributes) {
      switch (attibute.label) {
        case 'Hostname':
        this.name = attibute.value as string
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
        this.type = ItemTypeLabel[attibute.value as keyof typeof ItemTypeLabel] || attibute.value as string
        break
        case 'Version':
        this.version = attibute.value as string
        break
        case 'NetworkArea':
        this.networkArea = NetworkAreaLabel[attibute.value as keyof typeof NetworkAreaLabel] || attibute.value as string
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
        this.patchingDay = PatchingDayLabel[attibute.value as keyof typeof PatchingDayLabel] || attibute.value as string
        break
        case 'Domain':
        this.domain = DomainLabel[attibute.value as keyof typeof DomainLabel] || attibute.value as string
        break
        case 'Dns':
        this.dns = attibute.value as string
        break
      }
    }
    this.environmentUuid = item.incomingLinks?.find(link => link.linkType.label === LinkTypeLabel.COMPOSED_OF)?.sourceItemId
    this.communicatesWith = item.outgoingLinks?.filter(link => link.linkType.label === LinkTypeLabel.COMMUNICATE_WITH)
  }

  convertItemIntoHost(item: ItemDto): Host {
    return {
      uuid: item.uuid!,
      environmentUuid: item.incomingLinks?.find(link => link.linkType.label === LinkTypeLabel.COMPOSED_OF)?.sourceItemId,
      name: item.name,
      description: item.description,
      type: item.attributes?.find(attr => attr.label === "Type")?.value as string || "",
      status: item.attributes?.find(attr => attr.label === "Status")?.value as string || "",
      version: item.attributes?.find(attr => attr.label === "Version")?.value as string || "",
      certificate: item.attributes?.find(attr => attr.label === "Certificates")?.value as string || "",
      technology: item.attributes?.find(attr => attr.label === "Technology")?.value as string || "",
      domain: item.attributes?.find(attr => attr.label === "Domain")?.value as string || "",
      networkArea: item.attributes?.find(attr => attr.label === "NetworkArea")?.value as string || "",
      macAddress: item.attributes?.find(attr => attr.label === "MacAddress")?.value as string || "",
      vlan: item.attributes?.find(attr => attr.label === "Vlan")?.value as string || "",
      dns: item.attributes?.find(attr => attr.label === "Dns")?.value as string || "",
      patchingDay: item.attributes?.find(attr => attr.label === "PatchingDay")?.value as string || "",
      ipAddress: item.attributes?.find(attr => attr.label === "IpAddress")?.value as string || "",
      archiveDatetime: item.attributes?.find(attr => attr.label === "ArchiveDatetime")?.value as string || "",
      creationDatetime: item.attributes?.find(attr => attr.label === "CreationDatetime")?.value as string || "",
    }
  }

  mapToItemDto(): ItemDto {
    return {
      uuid: this.uuid,
      name: this.name,
      description: this.description,
      type: { label: ItemTypeLabel.HOST },
      attributes: [
        { label: "Type", value: this.type },
        { label: "Technology", value: this.technology },
        { label: "Version", value: this.version },
        { label: "NetworkArea", value: this.networkArea },
        { label: "MacAddress", value: this.macAddress },
        { label: "Certificates", value: this.certificate },
        { label: "IpAddress", value: this.ipAddress },
        { label: "PatchingDay", value: this.patchingDay },
        { label: "Domain", value: this.domain },
        { label: "Dns", value: this.dns },
      ],
      incomingLinks: [
        {
          linkType: { label: LinkTypeLabel.COMPOSED_OF },
          sourceItemId: this.environmentUuid!,
          sourceItemName: "",
          targetItemId: this.uuid!,
          targetItemName: this.name!,
        }
      ],
      outgoingLinks: this.communicatesWith || [],
      createdBy: "",
      createdDate: this.creationTimeStamp,
      lastModifiedBy: "",
      lastModifiedDate: this.creationTimeStamp,
    }
  }
}