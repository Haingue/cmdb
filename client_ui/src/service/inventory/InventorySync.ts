import type { InventoryServiceServerInfo, ItemDto, ItemTypeDto, LinkTypeDto, PaginatedResponseDto, ServerSentEventNotificationDto, UUID } from "./types"

const URL = 'http://localhost:8090/api/inventory'

/** Server **/
export async function getServerInfo(): Promise<InventoryServiceServerInfo> {
  const response = await fetch(`${URL}/actuator/info`);
  if (!response.ok) throw new Error("Failed to fetch server status");
  return response.json();
}

/** Item **/
export async function searchItems(
  itemName?: string,
  itemType?: string,
  pageNumber: number = 0,
  pageSize: number = 10
): Promise<PaginatedResponseDto<ItemDto>> {
  const queryParams = new URLSearchParams();
  if (itemName) queryParams.append("itemName", itemName);
  if (itemType) queryParams.append("itemType", itemType);
  queryParams.append("page", pageNumber.toString());
  queryParams.append("pageSize", pageSize.toString());

  const response = await fetch(`${URL}/item?${queryParams.toString()}`);
  if (!response.ok) throw new Error("Failed to fetch items");
  return response.json();
}

export async function getItemById(itemId: UUID): Promise<ItemDto> {
  const response = await fetch(`${URL}/item/${itemId}`);
  if (!response.ok) throw new Error("Failed to fetch item");
  return response.json();
}

export async function createNewItem(item: ItemDto): Promise<ItemDto> {
  const response = await fetch(`${URL}/item`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(item),
  });
  if (!response.ok) throw new Error("Failed to create item");
  return response.json();
}

/** ItemType  **/
export async function searchItemTypes(
  label?: string,
  pageNumber: number = 0,
  pageSize: number = 10
): Promise<PaginatedResponseDto<ItemTypeDto>> {
  const queryParams = new URLSearchParams();
  if (label) queryParams.append("label", label);
  queryParams.append("pageNumber", pageNumber.toString());
  queryParams.append("pageSize", pageSize.toString());

  const response = await fetch(`${URL}/item-type?${queryParams.toString()}`);
  if (!response.ok) throw new Error("Failed to fetch item types");
  return response.json();
}

export async function getItemTypeById(itemTypeId: UUID): Promise<ItemTypeDto> {
  const response = await fetch(`${URL}/item-type/${itemTypeId}`);
  if (!response.ok) throw new Error("Failed to fetch item type");
  return response.json();
}

/** LinkType  **/
export async function searchLinkTypes(
  label?: string,
  pageNumber: number = 0,
  pageSize: number = 10
): Promise<PaginatedResponseDto<LinkTypeDto>> {
  const queryParams = new URLSearchParams();
  if (label) queryParams.append("label", label);
  queryParams.append("pageNumber", pageNumber.toString());
  queryParams.append("pageSize", pageSize.toString());

  const response = await fetch(`${URL}/link-type?${queryParams.toString()}`);
  if (!response.ok) throw new Error("Failed to fetch link types");
  return response.json();
}

export async function getLinkTypeById(linkTypeId: UUID): Promise<LinkTypeDto> {
  const response = await fetch(`${URL}/link-type/${linkTypeId}`);
  if (!response.ok) throw new Error("Failed to fetch link type");
  return response.json();
}

export async function createNewLinkType(link: LinkTypeDto): Promise<LinkTypeDto> {
  const response = await fetch(`${URL}/link-type`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(link),
  });
  if (!response.ok) throw new Error("Failed to create link");
  return response.json();
}

/** Notification  **/
export function subscribeToNotifications(callback: (event: ServerSentEventNotificationDto) => void): EventSource {
  const eventSource = new EventSource(`${URL}/notification/subscribe`);
  eventSource.onmessage = (e) => callback(JSON.parse(e.data));
  return eventSource;
}


export default {getItemById, searchItems, getItemTypeById, searchItemTypes, getLinkTypeById, searchLinkTypes, createNewLinkType, subscribeToNotifications, getServerInfo}