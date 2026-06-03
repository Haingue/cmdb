import type { DateTime, UUID } from "../inventory/types"

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
  certificate?: string
  technology?: string
}
export type Host = Component & {
  domain?: string
  networkArea?: string
  macAddress?: string
  vlan?: string
  version?: string
  dns?: string
  patchingDay?: string
  ipAddress?: string
  archiveDatetime?: string
  creationDatetime?: string
  //? events: string[]
  communicatesWith?: Component[]
}
export type Software = Component & {
  softwareType?: string
  repositoryUrl?: string
  hosts?: Host[]
}

export type DashboardMetrics = {
  serverCount: number
  applicationCount: number
  projectCount: number
  activeUserCount: number
}

