import { addEdge, Background, BackgroundVariant, ControlButton, Controls, MiniMap, ReactFlow, useEdgesState, useNodesState } from "@xyflow/react";

import '@xyflow/react/dist/style.css';
import { useCallback, useEffect } from "react";
import { fetchProjects } from "../../service/inventory/inventory";

const initialNodes = [
    { id: '1', position: { x: 0, y: 0 }, data: { label: '1' } },
    { id: '2', position: { x: 0, y: 100 }, data: { label: '2' } },
];
const initialEdges = [{ id: 'e1-2', source: '1', target: '2' }];

const Map = () => {
    const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
    const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
  
    const onConnect = useCallback(
        (params) => setEdges((eds) => addEdge(params, eds)),
        [setEdges],
    );

    const loadProjects = async () => {
        const json = await fetchProjects()
        console.log(json)
        json.data.forEach(item => {
            const _nodes = [...nodes, { id: item.documentId, position: { x: 100, y: 0 }, data: { label: `${item.fullName}` } }]
            setNodes(_nodes)
        });
        return json
    }

    return (
    <>
        <h1>React Flow</h1>
        <div>
            <button onClick={() => loadProjects()}>Load project</button>
        </div>
        <div style={{ width: '60vw', height: '60vh' }}>
            <ReactFlow
                nodes={nodes}
                edges={edges}
                onNodesChange={onNodesChange}
                onEdgesChange={onEdgesChange}
                onConnect={onConnect}
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