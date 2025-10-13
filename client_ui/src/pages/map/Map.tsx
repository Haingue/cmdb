import { Background, BackgroundVariant, ControlButton, Controls, MiniMap, ReactFlow, useEdgesState, useNodesState, type Node, type NodeProps, type XYPosition } from "@xyflow/react";

import { useEffect } from "react";
import { searchItemTypes } from "../../service/inventory/InventorySync";
import PageTitle from "../../components/PageTitle";

import '@xyflow/react/dist/style.css';

const nodeTypes = {
  itemType: ({ data }: NodeProps) => {
    return (
      <div className="p-4 bg-white border border-gray-300 rounded-lg shadow-md">
        <p className="text-xs text-center">[Item type]</p>
        <h3 className="text-lg font-semibold text-center">{data.label}</h3>
        <p className="text-sm text-gray-600">{data.description}</p>
        <hr />
        <section>
          {data.attributes.map((attr: any, index: number) => (
            <div key={`${index}-${attr.uuid}`} className="mt-2">
              <p className="text-sm font-medium">{attr.label}</p>
              <p className="text-xs text-gray-500">{attr.description}</p>
            </div>
          ))}
        </section>
      </div>
    );
  }
};

const Map = () => {
    const [nodes, setNodes, onNodesChange] = useNodesState([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  
    const loadProjects = async () => {
      const json = await searchItemTypes()
      console.log(json)
      json.content
        .forEach((item, index) => {
            const _node = { id: item.uuid, type: 'itemType', data: { uuid: item.uuid, label: `${item.label}`, description: `${item.description}`, attributes: item.attributes } }
            addNode(_node, { x: Math.random() * 600, y: Math.random() * 900 })
        });
      return json
    }

    const addNode = (entity: any, position: XYPosition): Node => {
      const node: Node = {
        id: entity.id,
        type: entity.type,
        position: position,
        data: { ...entity.data },
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
        <div className=" h-[80vh] border border-gray-300 rounded-lg mt-4">
            <ReactFlow
                nodes={nodes}
                edges={edges}
                nodeTypes={nodeTypes}
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