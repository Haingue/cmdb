export type InventoryServiceServerInfo = {
  application: {
    name: string
    version: string
    description: string
  }
}

export type UUID = string;
export type DateTime = string;

export type AuditableDto = {
  uuid?: UUID;
  createdDate?: DateTime;
  createdBy?: UUID;
  lastModifiedBy?: UUID;
  lastModifiedDate?: DateTime;
}

export type AttributeDto = {
  label: string;
  attributeTypeId: UUID;
  value: string | number | boolean | undefined;
} & AuditableDto

export type AttributeTypeDto = {
  label: string;
  description: string;
  shortDescription: string;
  placeholder: string;
  regex: string;
  possibleValues: string[];
} & AuditableDto

export type ItemTypeDto = {
  label: string;
  description: string;
  attributes: AttributeTypeDto[];
} & AuditableDto

export type LinkTypeDto = {
  label: string;
} & AuditableDto

export type LinkDto = {
  linkType: LinkTypeDto;
  sourceItemId: UUID;
  targetItemId: ItemDto;
  description: string;
} & AuditableDto

export type ItemDto = {
  name?: string;
  description?: string;
  type?: ItemTypeDto;
  attributes?: AttributeDto[];
  outgoingLinks?: LinkDto[];
  incomingLinks?: LinkDto[];
} & AuditableDto

export type PaginatedResponseDto<T> = {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  empty: boolean;
}

export type ServerSentEventNotificationDto = {
  [key: string]: any;
}