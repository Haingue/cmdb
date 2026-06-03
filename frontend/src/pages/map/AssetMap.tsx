
import { Background, BackgroundVariant, ControlButton, Controls, MarkerType, MiniMap, ReactFlow, useEdgesState, useNodesState, type Edge, type Node, type ReactFlowInstance, type XYZPosition } from '@xyflow/react'
import { graphlib, layout } from 'dagre'
import { useCallback, useEffect, useRef, useState } from 'react'
import EdgeTypeFactory from '../../components/ReactFlow/EdgeFactory'
import NodeTypeFactory from '../../components/ReactFlow/NodeFactory'
import type { ItemDto, LinkDto } from '../../service/inventory/types'

const AssetMap = ({items}: {items: ItemDto[]}) => {
  const flowRef = useRef(null)
  const [reactFlowInstance, setReactFlowInstance] = useState<ReactFlowInstance | undefined>(undefined)
  const nodeWidth = 272// flowRef.current ? (flowRef.current as any).clientWidth : 50
  const nodeHeight = 136// flowRef.current ? (flowRef.current as any).clientHeight : 50
  const [isHorizontal, setIsHorizontal] =  useState(true)

  const [nodes, setNodes, onNodesChange] = useNodesState([])
  const [edges, setEdges, onEdgesChange] = useEdgesState([])
  
  const autoLayout = new graphlib.Graph().setDefaultEdgeLabel(() => ({}))

  const addNode = (item: ItemDto, position: XYZPosition): Node => {
    const node: Node = {
      id: item.uuid as string,
      position: position,
      type: item.type?.label || 'default',
      data: {
        name: item.name,
        label: item.name,
        description: item.description,
        attributes: item.attributes,
      }
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
      type: link.linkType.label || 'default',
      markerEnd: {
        type: MarkerType.Arrow,
        width: 30,
        height: 30,
      },
      // animated: true,
      data: link
    }
    // if (edges.filter((e: Edge) => e.id === edge.id).length < 2) {
      setEdges((eds) => eds.concat(edge))
    // }
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
        position: {
          x: nodeWithPosition.x - (nodeWidth / 2),
          y: nodeWithPosition.y - (nodeHeight / 2),
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
      reactFlowInstance?.fitView()
    },
    [nodes, edges],
  )
  
  const onClean = async () => {
    await setNodes([])
    await setEdges([])
  }
  
  const onLoad = async () => {
    await onClean()
    items?.forEach((item, key) => {
      const position = { x: key * 200, y: 350, z: 0 }
      if (key === 0) {
        position.x = 250
        position.y = 100
      }
      addNode(item, position)
      item.outgoingLinks?.forEach(link => addEdge(link))
      item.incomingLinks?.forEach(link => addEdge(link))
    });
    // onLayout()
    await reactFlowInstance?.fitView()
  }

  useEffect(() => {
    onLoad()
    .then(() => console.debug(`AssetMap: Loaded ${items.length} items [nodes: ${nodes.length}, edges: ${edges.length}]`))
  }, [items])

  return (
    <>
      <ReactFlow
          nodes={nodes}
          edges={edges}
          nodeTypes={NodeTypeFactory}
          edgeTypes={EdgeTypeFactory}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          ref={flowRef}
          onInit={(instance) => setReactFlowInstance(instance)}
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
        <MiniMap pannable zoomable nodeStrokeColor="black" maskStrokeWidth={1} maskStrokeColor="red" />
        <Background variant={BackgroundVariant.Cross} gap={12} size={1} />
      </ReactFlow>
    </>
  )
}


export default AssetMap