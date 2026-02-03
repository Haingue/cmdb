import { BaseEdge, EdgeLabelRenderer, getBezierPath, MarkerType, type EdgeProps } from "@xyflow/react";
import type { ReactNode } from "react";

export type TrafficEdge = EdgeProps & {
    onEdgeClick: () => void
}

function EdgeLabel({ transform, label }: { transform: string; label: ReactNode }) {
  return (
    <div
      style={{
        transform,
      }}
      className="nodrag nopan w-30 absolute text-xs font-semibold text-brand-strong dark:text-brand-soft"
    >
      {label}
    </div>
  );
}

const TrafficEdge = ({ id, label, sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition, data }: TrafficEdge) => {
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
      <BaseEdge id={id} path={edgePath} markerEnd={MarkerType.ArrowClosed} />
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
    </>
  );
}

export default TrafficEdge