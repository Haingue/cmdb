
const URL = 'http://localhost:8090'

/** Server **/
export async function getServerInfo(): Promise<{
  name: string
  version: string
  description: string
}> {
  const response = await fetch(`${URL}/actuator/info`);
  if (!response.ok) throw new Error("Failed to fetch server status");
  return response.json();
}

export default {getServerInfo}