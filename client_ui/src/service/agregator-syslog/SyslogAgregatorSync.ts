import { BACKEND_BASE_URL } from "../../configuration";
import type { SyslogServiceServerInfo } from "./types"

const URL = `${BACKEND_BASE_URL}/api/aggregator/syslog`

/** Server **/
export async function getServerInfo(): Promise<SyslogServiceServerInfo> {
  const response = await fetch(`${URL}/actuator/info`);
  if (!response.ok) throw new Error("Failed to fetch server status");
  return response.json();
}

export default {getServerInfo}