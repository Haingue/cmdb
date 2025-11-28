import { Background, BackgroundVariant, ControlButton, Controls, Handle, MiniMap, Position, ReactFlow, useEdgesState, useNodesState, type Edge, type Node, type NodeProps, type XYPosition } from "@xyflow/react";

import { useEffect } from "react";
import { searchItems } from "../../service/inventory/InventorySync";
import PageTitle from "../../components/PageTitle";

import '@xyflow/react/dist/style.css';
import type { AttributeDto, ItemDto, LinkDto } from "../../service/inventory/types";

const nodeTypes = {
  itemType: ({ data }: NodeProps) => {
    const item: ItemDto = data as ItemDto;
    return (
      <div className="p-4 bg-white border border-gray-300 rounded-lg shadow-md">
        <Handle
          type="source"
          position={Position.Left}
          onConnect={(params) => console.log('handle onConnect', params)}
          isConnectable={false}
        />
        <p className="text-xs text-center">[{item?.type?.label}]</p>
        <h3 className="text-lg font-semibold text-center">{item?.name}</h3>
        <p className="text-sm text-gray-600">{item?.description}</p>
        <hr />
        <section>
          {item?.attributes?.map((attr: AttributeDto, index: number) => (
            <div key={`${index}-${attr.uuid}`} className="mt-2">
              <p className="text-sm font-medium">{attr.label}</p>
              <p className="text-xs text-gray-500">{attr.value}</p>
            </div>
          ))}
        </section>
        <Handle
          type="target"
          position={Position.Right}
          onConnect={(params) => console.log('handle onConnect', params)}
          isConnectable={false}
        />
      </div>
    );
  }
};

const MapItems = () => {
    const [nodes, setNodes, onNodesChange] = useNodesState([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  
    const loadProjects = async () => {
      const json = await searchItems(undefined, undefined, 0, 100)
      console.log(json)
      json.content
        .filter((item, key) => json.content.indexOf(item) == key)
        .forEach((item, index) => {
            const _node = { id: item.uuid, type: 'itemType', data: { label: `${item.name}`, ...item } }
            addNode(_node, { x: Math.random() * 600, y: Math.random() * 900 })

            item.outgoingLinks?.forEach(link => addEdge(link))
        });
      return json
    }

    const addEdge = (link: LinkDto): Edge => {
      const edge: Edge = {
        id: `${link.sourceItemId}-${link.targetItemId}` as string,
        source: link.sourceItemId as string,
        target: link.targetItemId as string,
        label: link.linkType.label,
        data: link
      }
      setEdges((eds) => eds.concat(edge))
      return edge
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
      console.log('Load Items from API')
      loadProjects()
    }, [])

    return (
    <>
        <PageTitle title="React Flow" />
        <div>
            <button onClick={loadProjects}>Load project</button>
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

export default MapItems