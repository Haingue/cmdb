import { BACKEND_BASE_URL } from "../../configuration";
import type { ItemDto, PaginatedResponseDto } from "../inventory/types";
import type { BackendServerInfo, BusinessService, ApiProblem, ProjectCreationRequest, Environment, Host, Software, EnvironmentCreationRequest, DashboardMetrics } from "./types";

/** Server **/
export async function getServerInfo(): Promise<BackendServerInfo> {
  const response = await fetch(`${BACKEND_BASE_URL}/actuator/info`);
  if (!response.ok) throw new Error("Failed to fetch server status");
  return response.json();
}

/** Business Service **/
export async function searchBusinessService(name?: string): Promise<PaginatedResponseDto<ItemDto>> {
  const queryParams = new URLSearchParams({ itemTypeLabel: "BusinessService" });
  if (name) {
    queryParams.append("itemName", name);
  }
  const response = await fetch(`${BACKEND_BASE_URL}/service/item?${queryParams.toString()}`);
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

export async function createBusinessService(businessService: BusinessService): Promise<ItemDto> {
  const response = await fetch(`${BACKEND_BASE_URL}/service/business-service`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(businessService),
  });
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

/** Project **/
export async function searchProject(name?: string): Promise<PaginatedResponseDto<ItemDto>> {
  const queryParams = new URLSearchParams({ itemTypeLabel: "Project" });
  if (name) {
    queryParams.append("itemName", name);
  }
  const response = await fetch(`${BACKEND_BASE_URL}/service/item?${queryParams.toString()}`);
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

export async function createProject(project: ProjectCreationRequest): Promise<ItemDto> {
  const response = await fetch(`${BACKEND_BASE_URL}/service/project`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(project),
  });
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

/** Environment **/
export async function searchEnvironment(name?: string): Promise<PaginatedResponseDto<ItemDto>> {
  const queryParams = new URLSearchParams({ itemTypeLabel: "Environment" });
  if (name) {
    queryParams.append("itemName", name);
  }
  const response = await fetch(`${BACKEND_BASE_URL}/service/item?${queryParams.toString()}`);
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

export async function createEnvironment(EnvironmentCreationRequest: EnvironmentCreationRequest): Promise<ItemDto> {
  const response = await fetch(`${BACKEND_BASE_URL}/service/environment`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(EnvironmentCreationRequest),
  });
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

/** Host **/
export async function searchHost(name?: string): Promise<PaginatedResponseDto<ItemDto>> {
  const queryParams = new URLSearchParams({ itemTypeLabel: "Host" });
  if (name) {
    queryParams.append("itemName", name);
  }
  const response = await fetch(`${BACKEND_BASE_URL}/service/item?${queryParams.toString()}`);
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

export async function createHost(host: Host): Promise<ItemDto> {
  const response = await fetch(`${BACKEND_BASE_URL}/service/host`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(host),
  });
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

/** Software **/
export async function searchSoftware(name?: string): Promise<PaginatedResponseDto<ItemDto>> {
  const queryParams = new URLSearchParams({ itemTypeLabel: "Software" });
  if (name) {
    queryParams.append("itemName", name);
  }
  const response = await fetch(`${BACKEND_BASE_URL}/service/item?${queryParams.toString()}`);
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

export async function createSoftware(project: Software): Promise<ItemDto> {
  const response = await fetch(`${BACKEND_BASE_URL}/service/software`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(project),
  });
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

/** Dashboard **/
export async function getDashboardMetrics(): Promise<DashboardMetrics> {
  const response = await fetch(`${BACKEND_BASE_URL}/service/dashboard/metrics`);
  if (!response.ok) return Promise.reject(await response.json() as ApiProblem);
  return response.json();
}

export default {
  getServerInfo,
  createBusinessService,
  createProject,
  createEnvironment,
  createHost,
  createSoftware,
  getDashboardMetrics
};