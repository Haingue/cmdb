import { Handle, Position, type NodeProps } from '@xyflow/react';
import { useState } from 'react'
import type { Project } from '../../../service/backend/types';

const ProjectNodeType = ({ data }: NodeProps) => {
  const project: Project = data as Project;
  const [showProps, setShowProps] = useState<boolean>(false)
  return (
    <div className="p-4 bg-green-100 border border-green-300 rounded-lg shadow-md text-black">
      <p className="text-xs text-center cursor-pointer" onClick={() => setShowProps(!showProps)}>[Project]</p>
      <h3 className="text-lg font-semibold text-center">{project.name}</h3>
      <section className={`transition-all duration-300 ease-in-out ${showProps ? 'max-h-96 w-auto opacity-100 mt-2' : 'max-h-0 max-w-0 opacity-0 overflow-hidden'}`}>
        <hr />
        <ul className="ps-5 my-2 space-y-1 list-disc text-xs font-normal">
          <li>description: {project.description || 'String'}</li>
          <li>businessService: {'BusinessService'}</li>
          <li>fullName: {project.fullName || 'String'}</li>
          <li>owners: {project.owners?.name || 'UserGroup'}</li>
          <li>maintainers: {project.maintainers?.name || 'UserGroup'}</li>
          <li>environments: {project.environments?.length || 'Set&lt;Environment&gt;'}</li>
          <li>shortName: {project.shortName || 'String'}</li>
        </ul>
      </section>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
      <Handle type="target" position={Position.Left} />
      <Handle type="source" position={Position.Right} />
    </div>
  );
}

export default ProjectNodeType