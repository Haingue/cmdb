import { Handle, Position, type NodeProps } from '@xyflow/react';
import type Host from '../../models/Host';

const NodeTypeHost = ({ data }: NodeProps) => {
  const hostItem: Host = data as Host;
  return (
    <div className="p-4 bg-white border border-gray-300 rounded-lg shadow-md">
      <Handle
        type="source"
        position={Position.Right}
        onConnect={(params) => console.log('handle onConnect', params)}
        isConnectable={false}
      />
      <section>
        <p className="text-xs text-center">[{hostItem.domain || "Unknown"}]</p>
        <h2 className="text-lg font-semibold text-center">{hostItem.hostname || hostItem.ipAddress}</h2>
        <hr />
        <h3 className="text-md font-semibold text-center">{hostItem.ipAddress}</h3>
        <p className="text-sm text-gray-600">{hostItem.operatingSystem}</p>
      </section>
      <Handle
        type="target"
        position={Position.Left}
        onConnect={(params) => console.log('handle onConnect', params)}
        isConnectable={false}
      />
    </div>
  );
}

export default NodeTypeHost