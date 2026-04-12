import { BaseEdge, EdgeLabelRenderer, getBezierPath, getSmoothStepPath, MarkerType, useInternalNode, type EdgeProps } from "@xyflow/react";
import type { ReactNode } from "react";
import EdgeLabel from "./EdgeLabel";
import { getEdgeParams } from "../../../utils/ReactFlowUtils";

export type CommunicatesWithEdge = EdgeProps & {
    onEdgeClick: () => void
}

const PARTICLE_COUNT = 6
const ANIMATE_DURATION = 6

const CommunicatesWithEdge = ({ id, label, source, target, markerEnd, data }: CommunicatesWithEdge) => {
  const sourceNode = useInternalNode(source);
  const targetNode = useInternalNode(target);
 
  if (!sourceNode || !targetNode) {
    return null;
  }

  const { sourceX, sourceY, targetX, targetY, sourcePosition, targetPosition } = getEdgeParams(
    sourceNode,
    targetNode,
  );
  const [edgePath, labelX, labelY ] = getBezierPath({
    sourceX,
    sourceY,
    sourcePosition,
    targetX,
    targetY,
    targetPosition,
  });
  const description: ReactNode = data?.description as string || '??'
 
  return (
    <>
      <BaseEdge id={id} path={edgePath} markerEnd={markerEnd} />
      <EdgeLabelRenderer>
        <EdgeLabel
            label={label}
            transform={`translate(-50%, -100%) translate(${labelX}px,${labelY}px)`}/>
        {data && data.description != null && (
          <EdgeLabel
            transform={`translate(-50%, -100%) translate(${sourceX+60}px,${sourceY+30}px)`}
            label={description}
          />
        )}
      </EdgeLabelRenderer>

      {[...Array(PARTICLE_COUNT)].map((_, i) => (
          <ellipse
            key={`particle-${i}-${ANIMATE_DURATION}`}
            rx="5"
            ry="1.2"
            fill="#ff0073"
          >
            {/* The <animateMotion> element defines how an element moves along a motion path.  */}
            <animateMotion
              begin={`${i * (ANIMATE_DURATION / PARTICLE_COUNT)}s`}
              dur={`${ANIMATE_DURATION}s`}
              repeatCount="indefinite"
              rotate="auto"
              path={edgePath}
              calcMode="spline"
              keySplines="0.42, 0, 0.58, 1.0"
            />
          </ellipse>
        ))}
    </>
  );
}

export default CommunicatesWithEdge