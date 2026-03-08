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

export type User = {
  uuid?: UUID
  username?: string
  email?: string
  userGroups?: UserGroup[]
}

export type UserGroup = {
  name?: string
  email?: string
  description?: string
  owner?: UserGroup | null
  members?: User[]
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
  name?: string
  description?: string
  fullName?: string
  shortName?: string
  version?: string
  businessServiceName?: string
  owners?: UserGroup
  maintainers?: UserGroup
  environments?: Environment[]
}

export type ProjectCreationRequest = Request & {
  project: Project
  businessService: BusinessService
}

export type Environment = {
  uuid: UUID
  projectUuid?: UUID
  name?: string
  description?: string
  type?: string
  status?: string
  version?: string
  jiraTicket?: string
  location?: string
}

export type EnvironmentCreationRequest = Request & {
    environment: Environment
    projectUuid: UUID
}

export type Component = {
  uuid: UUID
  environmentUuid?: UUID
  name?: string
  description?: string
  type?: string
  status?: string
  version?: string
}
export type Host = Component & {
}
export type Software = Component & {
}