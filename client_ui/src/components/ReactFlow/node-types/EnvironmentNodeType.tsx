import { Handle, Position, type NodeProps } from "@xyflow/react";
import { useState } from "react";
import type { Environment } from "../../../service/backend/types";

const EnvironmentNodeType = ({ data }: NodeProps) => {
  const environment: Environment = data as Environment;
  const [showProps, setShowProps] = useState<boolean>(false)
  return (
    <div className="p-4 bg-yellow-100 border border-yellow-300 rounded-lg shadow-md text-black">
      <p className="text-xs text-center cursor-pointer" onClick={() => setShowProps(!showProps)}>[Environment]</p>
      <h3 className="text-lg font-semibold text-center">{environment.name}</h3>
      <section className={`transition-all duration-300 ease-in-out ${showProps ? 'max-h-96 w-auto opacity-100 mt-2' : 'max-h-0 max-w-0 opacity-0 overflow-hidden'}`}>
        <hr />
        <ul className="ps-5 my-2 space-y-1 list-disc text-xs font-normal">
          <li>type: {environment.type || 'EnvironmentType'}</li>
          <li>components: {'Set&lt;Component&gt;'}</li>
          <li>location: {environment.location || 'String'}</li>
          <li>status: {environment.status || 'EnvironmentStatus'}</li>
          <li>jiraTracker: {environment.jiraTicket || 'String'}</li>
        </ul>
      </section>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
}
export default EnvironmentNodeType