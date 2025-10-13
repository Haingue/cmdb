import type { ItemDto, ItemTypeDto, PaginatedResponseDto, ServerSentEventNotificationDto, UUID } from "./type"

const URL = 'http://localhost:8090/api/inventory'

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

export function subscribeToNotifications(callback: (event: ServerSentEventNotificationDto) => void): EventSource {
  const eventSource = new EventSource("${URL}/notification/subscribe");
  eventSource.onmessage = (e) => callback(JSON.parse(e.data));
  return eventSource;
}
