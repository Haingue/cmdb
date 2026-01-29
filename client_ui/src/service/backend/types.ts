import type { DateTime, ItemDto, UUID } from "../inventory/types"

export type BackendServerInfo = {
  application: {
    name: string
    version: string
    description: string
  }
}

export type ApiProblem = {
  type: string
  title: string
  status: number
  detail: string
  instance: string
  properties: object[]
}

export type Request = {
  uuid: UUID
  requestor: UUID
  requestTimestamp: DateTime
}

export type BusinessService = {
  uuid: UUID
  name: string
  abbreviation: string
}

export type Project = {
  uuid: UUID
  name: string
  description: string
  fullname: string
  shortname: string
  version: string
  businessService: BusinessService
  owners: string
  maintainers: string
}

export type ProjectCreationRequest = Request & {
  project: Project
  businessService: BusinessService
}