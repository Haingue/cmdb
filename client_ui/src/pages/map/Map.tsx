import { Background, BackgroundVariant, ControlButton, Controls, MiniMap, ReactFlow, useEdgesState, useNodesState, type Node, type NodeProps, type XYPosition } from "@xyflow/react";

import { useCallback, useEffect } from "react";
import { fetchProjects } from "../../service/inventory/inventory";
import PageTitle from "../../components/PageTitle";

import '@xyflow/react/dist/style.css';

const listOfEntities = [
  {
    id: 'xxxx-xxxx-xxxx',
    name: 'PLC-1',
    type: 'PLC',
    description: 'This is a PLC deploy on area1',
    location: 'Area 1',
    communicateWith: ['xxxx-xxxx-xxxz']
  },
  {
    id: 'xxxx-xxxx-xxxy',
    name: 'PLC2',
    type: 'PLC',
    description: 'This is a PLC deploy on area1',
    location: 'Area 1',
    communicateWith: ['xxxx-xxxx-xxxz']
  },
  {
    id: 'xxxx-xxxx-xxxz',
    name: 'Gateway1',
    type: 'Gateway',
    description: 'This is the gateway of PLCs for area1',
    location: 'Area 1',
    communicateWith: ['xxxx-xxxx-xxxx', 'xxxx-xxxx-xxxy', 'xxxx-xxxx-xxyx']
  },
  {
    id: 'xxxx-xxxx-xxyx',
    name: 'PlcService',
    type: 'WebAPI',
    description: 'This is the web API to manage PLC of Area 1',
    location: 'ComputerRoom 1',
    communicateWith: ['xxxx-xxxx-xxxz', 'xxxx-xxxx-xxyy']
  },
  {
    id: 'xxxx-xxxx-xxyy',
    name: 'MyApp',
    type: 'WebApplication',
    description: 'This is the web UI to perform business logic using PLC on several areas',
    location: 'ComputerRoom 1',
    communicateWith: ['xxxx-xxxx-xxyx']
  }
]

const initialNodes = [
    { id: '1', position: { x: 0, y: 0 }, data: { label: '1' } },
    { id: '2', position: { x: 0, y: 100 }, data: { label: '2' } },
];
const initialEdges = [{ id: 'e1-2', source: '1', target: '2' }];

const Map = () => {
    const [nodes, setNodes, onNodesChange] = useNodesState([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  
    const loadProjects = async () => {
      /*
      const json = await fetchProjects()
      console.log(json)
      json.data.forEach(item => {
          const _nodes = [...nodes, { id: item.documentId, position: { x: 100, y: 0 }, data: { label: `${item.fullName}` } }]
          setNodes(_nodes)
      });
      return json
      */
      const json = [...listOfEntities]
      json.forEach((entity, idx) => {
        const newNode = addNode(entity, { x: idx * 200, y: 0 })

      })
    }

    const addNode = (entity: any, position: XYPosition): Node => {
      const node: Node = {
        id: entity.id,
        position: position,
        data: { label: entity.name, ...entity },
        origin: [0.5, 0.0]
      }
      setNodes((nds) => nds.concat(node))
      return node
    }

    useEffect(() => {
      loadProjects()
    }, [])

    return (
    <>
        <PageTitle title="React Flow" />
        <div>
            <button onClick={() => loadProjects()}>Load project</button>
        </div>
        <div style={{ width: '60vw', height: '60vh' }}>
            <ReactFlow
                nodes={nodes}
                edges={edges}
                onNodesChange={onNodesChange}
                onEdgesChange={onEdgesChange}
            >
                <Controls>
                    <ControlButton onClick={() => alert('Reload states ✨')}>
                        <span>♻</span>
                    </ControlButton>
                </Controls>
                <MiniMap pannable zoomable />
                <Background variant={BackgroundVariant.Dots} gap={12} size={1} />
            </ReactFlow>
        </div>
    </>
  )
}

export default Map