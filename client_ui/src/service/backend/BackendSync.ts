import { BACKEND_BASE_URL } from "../../configuration";
import type { ItemDto, PaginatedResponseDto } from "../inventory/types";
import type { BackendServerInfo, BusinessService, ApiProblem, Project, ProjectCreationRequest } from "./types";

/** Server **/
export async function getServerInfo(): Promise<BackendServerInfo> {
  const response = await fetch(`${BACKEND_BASE_URL}/actuator/info`);
  if (!response.ok) throw new Error("Failed to fetch server status");
  return response.json();
}

/** Business Service **/
export async function searchBusinessService(name?: string): Promise<PaginatedResponseDto<ItemDto>> {
  const queryParams = new URLSearchParams({ itemType: "BusinessService" });
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
  const queryParams = new URLSearchParams({ itemType: "Project" });
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

export default {getServerInfo, createBusinessService}