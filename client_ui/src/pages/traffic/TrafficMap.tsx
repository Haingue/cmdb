
import { Background, BackgroundVariant, MiniMap, ReactFlow, useEdgesState, useNodesState, type Edge, type XYZPosition } from '@xyflow/react'
import { useEffect } from 'react'
import NodeTypeHost from '../../components/ReactFlow/NodeTypeHost'
import type { ItemDto, LinkDto } from '../../service/inventory/types'
import Host from '../../models/Host'

const TrafficMap = ({items}: {items: ItemDto[]}) => {
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);

  const loadItems = async () => {
    items?.forEach((item, key) => {
      addNode(item, { x: key * 300, y: 0, z: 100 })
      item.outgoingLinks?.forEach(link => addEdge(link))
    });
  }

  const addNode = (item: ItemDto, position: XYZPosition): Node => {
    const node: Node = {
      id: item.uuid,
      type: 'host',
      position: position,
      data: new Host(item)
    }
    setNodes((nds) => nds.concat(node))
    return node
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
  
  useEffect(()=>{
    loadItems()
  }, [])

  return (
    <>
      <ReactFlow
          nodes={nodes}
          edges={edges}
          nodeTypes={{host: NodeTypeHost}}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
      >
          <MiniMap pannable zoomable />
          <Background variant={BackgroundVariant.Cross} gap={12} size={1} />
      </ReactFlow>
    </>
  )
}


export default TrafficMap