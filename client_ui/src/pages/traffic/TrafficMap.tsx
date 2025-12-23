
import { Background, BackgroundVariant, ControlButton, Controls, MiniMap, ReactFlow, useEdgesState, useNodesState, type Edge, type Node, type XYZPosition } from '@xyflow/react'
import { useCallback, useEffect, useRef, useState } from 'react'
import NodeTypeHost from '../../components/ReactFlow/NodeTypeHost'
import type { ItemDto, LinkDto } from '../../service/inventory/types'
import Host from '../../models/Host'
import { graphlib, layout } from 'dagre'

const TrafficMap = ({items}: {items: ItemDto[]}) => {
  const flowRef = useRef(null)
  const nodeWidth = flowRef.current ? (flowRef.current as any).clientWidth : 100
  const nodeHeight = flowRef.current ? (flowRef.current as any).clientHeight : 100
  const [isHorizontal, setIsHorizontal] =  useState(true)

  const [nodes, setNodes, onNodesChange] = useNodesState([])
  const [edges, setEdges, onEdgesChange] = useEdgesState([])
  
  const autoLayout = new graphlib.Graph().setDefaultEdgeLabel(() => ({}))

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

  const getLayoutedElements = (nodes: Node[], edges: Edge[], direction = 'TB'): { nodes: Node[], edges: Edge[] } => {
    const _isHorizontal = direction === 'LR';
    autoLayout.setGraph({ rankdir: direction });
  
    nodes.forEach((node: Node) => {
      autoLayout.setNode(node.id, { width: nodeWidth, height: nodeHeight });
    });
  
    edges.forEach((edge) => {
      autoLayout.setEdge(edge.source, edge.target);
    });
  
    layout(autoLayout);
  
    const newNodes = nodes.map((node: Node) => {
      const nodeWithPosition = autoLayout.node(node.id);
      const newNode = {
        ...node,
        targetPosition: _isHorizontal ? 'left' : 'top',
        sourcePosition: _isHorizontal ? 'right' : 'bottom',
        // We are shifting the dagre node position (anchor=center center) to the top left
        // so it matches the React Flow node anchor point (top left).
        position: {
          x: nodeWithPosition.x - nodeWidth / 5,
          y: nodeWithPosition.y - nodeHeight / 5,
          z: 0,
        },
      };
  
      return newNode;
    });
  
    return { nodes: newNodes, edges };
  };
  
  const onChangeDirection = () => {
    setIsHorizontal(!isHorizontal)
    onLayout()
  }

  const onLayout = useCallback(
    () => {
      const { nodes: layoutedNodes, edges: layoutedEdges } = getLayoutedElements(
        nodes,
        edges,
        isHorizontal ? 'LR' : 'TB',
      );
      setNodes([...layoutedNodes])
      setEdges([...layoutedEdges])
    },
    [nodes, edges],
  )
  
  const onClean = () => {
    setNodes([])
    setEdges([])
  }
  
  const onLoad = () => {
    onClean()
    items?.forEach((item, key) => {
      const position = { x: key * 10, y: 350, z: 0 }
      if (key === 0) {
        position.x = 350
        position.y = 100
      }
      addNode(item, position)
      item.outgoingLinks?.forEach(link => addEdge(link))
      item.incomingLinks?.forEach(link => addEdge(link))
    });
    // onLayout()
  }

  useEffect(() => {
    onLoad()
  }, [items])

  return (
    <>
      <ReactFlow
          nodes={nodes}
          edges={edges}
          nodeTypes={{host: NodeTypeHost}}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          ref={flowRef}
      >
        <Controls>
          <ControlButton onClick={() => onChangeDirection()}>
            {isHorizontal && <span>↕</span>}
            {!isHorizontal && <span>↔</span>}
          </ControlButton>
          <ControlButton onClick={() => onLoad()}>
            <span>🔄</span>
          </ControlButton>
          <ControlButton onClick={() => onClean()}>
            <span>🗑</span>
          </ControlButton>
        </Controls>
        <MiniMap pannable zoomable />
        <Background variant={BackgroundVariant.Cross} gap={12} size={1} />
      </ReactFlow>
    </>
  )
}


export default TrafficMap