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
    const itemTypes = [
      {label: "Business Service", icon: "⚙️", counter: 12, color: "bg-blue-100"},
      {label: "Environment", icon: "🌐", counter: 5, color: "bg-green-100"},
      {label: "Host", icon: "🖥️", counter: 20, color: "bg-yellow-100"},
      {label: "Software", icon: "📦", counter: 8, color: "bg-purple-100"}
    ]

    const [nodes, setNodes, onNodesChange] = useNodesState([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  
    const loadProjects = async () => {
      const json = await searchItemTypes()
      console.log(json)
      json.content
        .forEach((item) => {
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
            <button onClick={loadProjects}>Load project</button>
        </div>
        <div className="mb-4 mt-2 p-4 bg-neutral-100 border border-neutral-300 rounded">
          <input type="text" placeholder="Search..." className="w-full p-2 border border-gray-300 rounded" />
          <label>Categories</label>
          <div>
            {itemTypes.map((category) => (
              <div key={category.label} className={`inline-block mr-4 mb-2 px-3 py-1 ${category.color} border border-gray-300 rounded cursor-pointer`}>
                {category.icon} {category.label} ({category.counter})
              </div>
            ))}
          </div>
          <div>
            <label>Filters actifs</label>
            <div className="inline-block ml-2 px-3 py-1 bg-blue-100 border border-gray-300 rounded cursor-pointer">
              <span>⚙️ Business Service</span>
            </div>
          </div>
        </div>
        <div className="h-[70vh] border border-gray-300 rounded-lg mt-4">
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