import { Handle, Position, type NodeProps } from '@xyflow/react';
import { useState } from 'react'
import type { BusinessService } from '../../../service/backend/types';

const BusinessServiceNodeType = ({ data }: NodeProps) => {
  const businessService: BusinessService = data as BusinessService;

  const [showProps, setShowProps] = useState<boolean>(false)
  return (
    <div className="p-4 bg-brand-softer border border-brand-medium rounded-lg shadow-md text-black">
      <p className="text-xs text-center cursor-pointer" onClick={() => setShowProps(!showProps)}>[Business Service]</p>
      <h3 className="text-lg font-semibold text-center">{businessService.name}</h3>
      <section className={`transition-all duration-300 ease-in-out ${showProps ? 'max-h-96 w-auto opacity-100 mt-2' : 'max-h-0 max-w-0 opacity-0 overflow-hidden'}`}>
        <hr />
        <ul className="ps-5 my-2 space-y-1 list-disc text-xs font-normal">
          <li>abbreviation: {businessService.abbreviation || 'String'}</li>
          <li>name: {businessService.name || 'String'}</li>
        </ul>
      </section>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
      <Handle type="target" position={Position.Left} />
      <Handle type="source" position={Position.Right} />
    </div>
  );
}
export default BusinessServiceNodeType