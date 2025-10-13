export type UUID = string;
export type DateTime = string;

export type AttributeDto = {
  uuid: UUID;
  label: string;
  attributeTypeId: UUID;
  value: string;
  createdDate: DateTime;
  createdBy: UUID;
  lastModifiedBy: UUID;
  lastModifiedDate: DateTime;
}

export type AttributeTypeDto = {
  uuid: UUID;
  label: string;
  description: string;
  shortDescription: string;
  placeholder: string;
  regex: string;
  possibleValues: string[];
  createdDate: DateTime;
  createdBy: UUID;
  lastModifiedBy: UUID;
  lastModifiedDate: DateTime;
}

export type ItemTypeDto = {
  uuid: UUID;
  label: string;
  description: string;
  attributes: AttributeTypeDto[];
  createdDate: DateTime;
  createdBy: UUID;
  lastModifiedBy: UUID;
  lastModifiedDate: DateTime;
}

export type LinkTypeDto = {
  label: string;
}

export type LinkDto = {
  linkType: LinkTypeDto;
  fromItemId: UUID;
  toItemId: ItemDto;
  description: string;
}

export type ItemDto = {
  uuid: UUID;
  name: string;
  description: string;
  type: ItemTypeDto;
  attributes: AttributeDto[];
  fromLinks: LinkDto[];
  toLinks: LinkDto[];
  createdDate: DateTime;
  createdBy: UUID;
  lastModifiedBy: UUID;
  lastModifiedDate: DateTime;
}

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