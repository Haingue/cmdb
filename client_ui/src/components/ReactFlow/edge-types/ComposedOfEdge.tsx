import { BaseEdge, EdgeLabelRenderer, getBezierPath, getSmoothStepPath, MarkerType, useInternalNode, type EdgeProps } from "@xyflow/react";
import type { ReactNode } from "react";
import EdgeLabel from "./EdgeLabel";
import { getEdgeParams } from "../../../utils/ReactFlowUtils";

export type ComposedOfEdge = EdgeProps & {
    onEdgeClick: () => void
}

const ComposedOfEdge = ({ id, label, source, target, markerEnd, data }: ComposedOfEdge) => {
  const sourceNode = useInternalNode(source);
  const targetNode = useInternalNode(target);
 
  if (!sourceNode || !targetNode) {
    return null;
  }

  const { sourceX, sourceY, targetX, targetY, sourcePosition, targetPosition } = getEdgeParams(
    sourceNode,
    targetNode,
  );

  const [edgePath, labelX, labelY ] = getSmoothStepPath({
    sourceX,
    sourceY,
    sourcePosition,
    targetX,
    targetY,
    targetPosition,
  });
  const description: ReactNode = data?.description as string
 
  return (
    <>
      <BaseEdge id={id} path={edgePath} markerEnd={markerEnd} />
      <EdgeLabelRenderer>
        <EdgeLabel
            label={label}
            textClassName="text-black-strong dark:text-black-soft"
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

export default ComposedOfEdge