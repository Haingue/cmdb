import { Handle, Position, type NodeProps } from "@xyflow/react";
import { useState } from "react";
import type { Software } from "../../../service/backend/types";

const SoftwareNodeType = ({ data }: NodeProps) => {
  const software: Software = data as Software;
  const [showProps, setShowProps] = useState<boolean>(false)
  const [showPropsDetails, setShowPropsDetails] = useState<boolean>(false)
  return (
    <div className="p-4 bg-red-100 border border-red-300 rounded-lg shadow-md text-black">
      <p className="text-xs text-center cursor-pointer" onClick={() => setShowProps(!showProps)}>[Software]</p>
      <h3 className="text-lg font-semibold text-center">{software.name}</h3>
      <section className={`transition-all duration-300 ease-in-out ${showProps ? 'max-h-96 w-auto opacity-100 mt-2' : 'max-h-0 max-w-0 opacity-0 overflow-hidden'}`}>
        <hr />
        <ul className="ps-5 my-2 space-y-1 list-disc text-xs font-normal">
          <li><span className="cursor-pointer" onClick={() => setShowPropsDetails(!showPropsDetails)}>[include component props]</span>
            <ul className={`ps-5 my-2 space-y-1 list-disc transition-all duration-300 ease-in-out ${showPropsDetails ? 'max-h-96 w-auto opacity-100 mt-2' : 'max-h-0 max-w-0 opacity-0 overflow-hidden'}`}>
              <li>uuid: {software.uuid || 'UUID'}</li>
              <li>version: {software.version || 'Version'}</li>
              <li>name: {software.name || 'String'}</li>
              <li>creationTimeStamp: {'LocalDateTime'}</li>
              <li>description: {software.description || 'String'}</li>
              <li>type: {software.type || 'ComponentType'}</li>
              <li>certificate: {'String'}</li>
              <li>technology: {'Technology'}</li>
            </ul>
          </li>
          <li>host: Host</li>
        </ul>
      </section>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
      <Handle type="target" position={Position.Left} />
      <Handle type="source" position={Position.Right} />
    </div>
  );
}
export default SoftwareNodeType