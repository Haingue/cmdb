import { Handle, Position, type NodeProps } from "@xyflow/react";
import { useState } from "react";
import HostModel from "../../../models/Host";
import { DomainLabel, NetworkAreaLabel } from "../../../service/backend/constants";

const HostNodeType = ({ data }: NodeProps) => {
  const itemDto = data as ItemDto
  const host: HostModel = new HostModel(itemDto);

  const [showProps, setShowProps] = useState<boolean>(false)
  const [showPropsDetails, setShowPropsDetails] = useState<boolean>(false)

  let networkIcon = '🌐'
  switch (host.networkArea) {
    case NetworkAreaLabel.IT:
      networkIcon = '🌐'
      break;
    case NetworkAreaLabel.OT:
      networkIcon = '🔒'
      break;
    case NetworkAreaLabel.DMZ:
      networkIcon = '🛡️'
      break;
  }
  let domainIcon = '🖥️'
  switch (host.domain) {
    case DomainLabel.COMMON:
      domainIcon = '🖥️'
      break;
    case DomainLabel.MANUFACTURING:
      domainIcon = '🏭'
      break;
  }

  return (
    <div className="p-4 bg-purple-100 border border-purple-300 rounded-lg shadow-md text-black">
      <p className="text-xs text-center cursor-pointer" onClick={() => setShowProps(!showProps)}>[Host]</p>
      <h3 className="text-lg font-semibold text-center">{domainIcon} {host.name}</h3>
      <section className={`transition-all duration-300 ease-in-out ${showProps ? 'max-h-96 w-auto opacity-100 mt-2' : 'max-h-0 max-w-0 opacity-0 overflow-hidden'}`}>
        <hr />
        <ul className="ps-5 my-2 space-y-1 list-disc text-xs font-normal">
          <li><span className="cursor-pointer" onClick={() => setShowPropsDetails(!showPropsDetails)}>[include component props]</span>
            <ul className={`ps-5 my-2 space-y-1 list-disc transition-all duration-300 ease-in-out ${showPropsDetails ? 'max-h-96 w-auto opacity-100 mt-2' : 'max-h-0 max-w-0 opacity-0 overflow-hidden'}`}>
              <li>uuid: {host.uuid || 'UUID'}</li>
              <li>version: {host.version || 'Version'}</li>
              <li>name: {host.name || 'String'}</li>
              <li>creationTimeStamp: {host.creationTimeStamp || 'LocalDateTime'}</li>
              <li>description: {'String'}</li>
              <li>type: {host.type || 'ComponentType'}</li>
              <li>certificate: {host.certificate || 'String'}</li>
              <li>technology: {host.technology || 'Technology'}</li>
            </ul>
          </li>
          <li>patchingDay: {host.patchingDay || 'DayOfWeek'}</li>
          <li>hostname: {host.name || 'String'}</li>
          <li>dns: {host.dns || 'String'}</li>
          <li>domain: {domainIcon} {host.domain || 'ActiveDirectoryDomainName'}</li>
          <li>networkArea: {networkIcon} {host.networkArea || 'NetworkArea'}</li>
          <li>ipAddress: {host.ipAddress || 'InetAddress'}</li>
          <li>macAddress: {host.macAddress || 'String'}</li>
          <li>vlan: {host.vlan || 'String'}</li>
        </ul>
      </section>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
      <Handle type="target" position={Position.Left} />
      <Handle type="source" position={Position.Right} />
    </div>
  );
}
export default HostNodeType