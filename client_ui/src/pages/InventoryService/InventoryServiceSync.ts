export type ItemTypeDto = {
  uuid: string,
  label: string,
  description: string,
  attributes: AttributeTypeDto[],
  createdDate: string,
  createdBy: string,
  lastModifiedBy: string,
  lastModifiedDate: string,
}

export type AttributeTypeDto = {
  uuid:string,
  label:string,
  description:string,
  shortDescription:string,
  placeholder:string,
  regex:string,
  possibleValues: string[],
  items:string,
  createdDate:string,
  createdBy:string,
  lastModifiedBy:string,
  lastModifiedDate:string,
}

export type ItemDto = {
  uuid: string,
  name: string,
  description: string,
  attributes: AttributeDto[],
  fromLinks: LinkDto[],
  toLinks: LinkDto[],
  createdDate: string,
  createdBy: string,
  lastModifiedBy: string,
  lastModifiedDate: string,
}

export type AttributeDto = {
  uuid: string,
  label: string,
  attributeTypeId: string,
  value: string,
  createdDate: string,
  createdBy: string,
  lastModifiedBy: string,
  lastModifiedDate: string,
}

export type LinkDto = {
  linkType: LinkTypeDto,
  fromItemId: string,
  toItemId: string,
  description: string,
}

export type LinkTypeDto = {
  label: string,
}

class InventoryServiceSync {

  API_URL = 'http://localhost:8080/api'

  fetchItemTypes = async (): Promise<ItemTypeDto[]> => {
    const response: Response = await fetch(`${this.API_URL}/api/item-types`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
    if (!response.ok) {
      throw new Error(`Error fetching item types: ${response.statusText}`)
    }
    const itemTypes: ItemTypeDto[] = await response.json()
    return itemTypes
  }
}
const inventoryServiceSync = new InventoryServiceSync()
export default inventoryServiceSync as InventoryServiceSync