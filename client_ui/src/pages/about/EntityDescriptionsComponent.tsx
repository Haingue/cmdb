import { Background, BackgroundVariant, Handle, Position, ReactFlow, useEdgesState, useNodesState } from "@xyflow/react"
import { useState } from "react";

const BusinessServiceNodeType = ({ data }: any) => {
  return (
    <div className="p-4 bg-blue-100 border border-blue-300 rounded-lg shadow-md">
      <p className="text-xs text-center">[Business Service]</p>
      <h3 className="text-lg font-semibold text-center">{data.label}</h3>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
}
const ProjectNodeType = ({ data }: any) => {
  const [showProps, setShowProps] = useState<boolean>(false)
  return (
    <div className="p-4 bg-green-100 border border-green-300 rounded-lg shadow-md">
      <p className="text-xs text-center cursor-pointer" onClick={() => setShowProps(!showProps)}>[Project]</p>
      <h3 className="text-lg font-semibold text-center">{data.label}</h3>
      <section className={`transition-all duration-300 ease-in-out ${showProps ? 'max-h-96 w-auto opacity-100 mt-2' : 'max-h-0 max-w-0 opacity-0 overflow-hidden'}`}>
        <hr />
        <ul className="mt-2 mb-2 list-disc">
          <li>description: String</li>
          <li>businessService: BusinessService</li>
          <li>fullName: String</li>
          <li>owners: UserGroup</li>
          <li>maintainers: UserGroup</li>
          <li>environments: Set&lt;Environment&gt;</li>
          <li>shortName: String</li>
        </ul>
      </section>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
}
const EnvironmentNodeType = ({ data }: any) => {
  return (
    <div className="p-4 bg-yellow-100 border border-yellow-300 rounded-lg shadow-md">
      <p className="text-xs text-center">[Environment]</p>
      <h3 className="text-lg font-semibold text-center">{data.label}</h3>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
}
const HostNodeType = ({ data }: any) => {
  return (
    <div className="p-4 bg-purple-100 border border-purple-300 rounded-lg shadow-md">
      <p className="text-xs text-center">[Host]</p>
      <h3 className="text-lg font-semibold text-center">{data.label}</h3>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
}
const SoftwareNodeType = ({ data }: any) => {
  return (
    <div className="p-4 bg-red-100 border border-red-300 rounded-lg shadow-md">
      <p className="text-xs text-center">[Software]</p>
      <h3 className="text-lg font-semibold text-center">{data.label}</h3>
      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
}

const nodeTypes = {
  businessservice: BusinessServiceNodeType,
  project: ProjectNodeType,
  environment: EnvironmentNodeType,
  host: HostNodeType,
  software: SoftwareNodeType,
}

const EntityDescriptionsComponent = () => {
  const [nodes, setNodes, onNodesChange] = useNodesState([
    { id: 'bs1', type: 'businessservice', data: {label: 'CMDB (FR)'}, position: { x: 50, y: 50 } },
    { id: 'bs2', type: 'businessservice', data: {label: 'CMDB (UK)'}, position: { x: 100, y: 70 } },
    { id: 'p1', type: 'project', data: {label: 'CMDB'}, position: { x: 250, y: 35 } },
    { id: 'e1', type: 'environment', data: {label: 'DEV'}, position: { x: 100, y: 200 } },
    { id: 'e2', type: 'environment', data: {label: 'ACC'}, position: { x: 350, y: 200 } },
    { id: 'e3', type: 'environment', data: {label: 'PROD'}, position: { x: 550, y: 200 } },
    
    { id: 'h1', type: 'host', data: {label: 'k8s-cluster-acc'}, position: { x: 50, y: 350 } },
    { id: 'h2', type: 'host', data: {label: 'database-cluster-acc'}, position: { x: 250, y: 350 } },
    { id: 's1', type: 'software', data: {label: 'cmdb-backend-acc'}, position: { x: 50, y: 500 } },
    { id: 's2', type: 'software', data: {label: 'cmdb-frontend-acc'}, position: { x: 200, y: 500 } },
    { id: 's3', type: 'software', data: {label: 'cmdb-db-acc'}, position: { x: 350, y: 500 } },

    { id: 'h3', type: 'host', data: {label: 'k8s-cluster-prod'}, position: { x: 500, y: 350 } },
    { id: 'h4', type: 'host', data: {label: 'database-cluster-prod'}, position: { x: 700, y: 350 } },
    { id: 's5', type: 'software', data: {label: 'cmdb-backend-prod'}, position: { x: 450, y: 500 } },
    { id: 's6', type: 'software', data: {label: 'cmdb-frontend-prod'}, position: { x: 600, y: 500 } },
    { id: 's7', type: 'software', data: {label: 'cmdb-db-prod'}, position: { x: 750, y: 500 } },
  ]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([
    { id: 'p1-bs1', source: 'p1', target: 'bs1', animated: true, label: 'implements' },
    { id: 'p1-bs2', source: 'p1', target: 'bs2', animated: true, label: 'implements' },

    { id: 'e1-p1', source: 'p1', target: 'e1', animated: true, label: 'compose of' },
    { id: 'e2-p1', source: 'p1', target: 'e2', animated: true, label: 'compose of' },
    { id: 'e3-p1', source: 'p1', target: 'e3', animated: true, label: 'compose of' },

    { id: 'e2-h1', source: 'e2', target: 'h1', animated: true, label: 'compose of' },
    { id: 'e2-h2', source: 'e2', target: 'h2', animated: true, label: 'compose of' },
    { id: 'h1-s1', source: 'h1', target: 's1', animated: true, label: 'hosts' },
    { id: 'h1-s2', source: 'h1', target: 's2', animated: true, label: 'hosts' },
    { id: 'h2-s3', source: 'h2', target: 's3', animated: true, label: 'hosts' },
    { id: 'e2-s1', source: 'e2', target: 's1', animated: true, label: 'compose of' },
    { id: 'e2-s2', source: 'e2', target: 's2', animated: true, label: 'compose of' },
    { id: 'e2-s3', source: 'e2', target: 's3', animated: true, label: 'compose of' },

    { id: 'e3-h3', source: 'e3', target: 'h3', animated: true, label: 'compose of' },
    { id: 'e3-h4', source: 'e3', target: 'h4', animated: true, label: 'compose of' },
    { id: 'h3-s5', source: 'h3', target: 's5', animated: true, label: 'hosts' },
    { id: 'h3-s6', source: 'h3', target: 's6', animated: true, label: 'hosts' },
    { id: 'h4-s7', source: 'h4', target: 's7', animated: true, label: 'hosts' },
    { id: 'e3-s5', source: 'e3', target: 's5', animated: true, label: 'compose of' },
    { id: 'e3-s6', source: 'e3', target: 's6', animated: true, label: 'compose of' },
    { id: 'e3-s7', source: 'e3', target: 's7', animated: true, label: 'compose of' },
  ]);
  return (
      <>
        <h2 className="text-2xl font-semibold mb-2">Entity descriptions</h2>
        <div className="w-2/3 h-[60vh] min-w-[60vh] m-auto">
          <ReactFlow
            className="border border-gray-300 rounded-lg mt-4"
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            nodeTypes={nodeTypes}
          >
            <Background variant={BackgroundVariant.Dots} gap={12} size={1} />
          </ReactFlow>
        </div>
      </>
    )
}

export default EntityDescriptionsComponent