import { BACKEND_BASE_URL } from "../../configuration";
import type { BackendServerInfo } from "./types";

/** Server **/
export async function getServerInfo(): Promise<BackendServerInfo> {
  const response = await fetch(`${BACKEND_BASE_URL}/actuator/info`);
  if (!response.ok) throw new Error("Failed to fetch server status");
  return response.json();
}

export default {getServerInfo}