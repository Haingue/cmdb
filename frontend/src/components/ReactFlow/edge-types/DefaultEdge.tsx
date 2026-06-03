import { BaseEdge, EdgeLabelRenderer, getBezierPath, MarkerType, type EdgeProps } from "@xyflow/react";
import type { ReactNode } from "react";
import EdgeLabel from "./EdgeLabel";

export type DefaultEdgeProps = EdgeProps & {
    onEdgeClick: () => void
}

const DefaultEdge = ({ id, label, sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition, data }: DefaultEdgeProps) => {
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

export default DefaultEdge