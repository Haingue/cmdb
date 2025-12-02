import type { NodeProps } from '@xyflow/react'
import React from 'react'
import NodeTypeHost from './NodeTypeHost'

const NodeFactory = (nodeType: string, nodeProps :NodeProps) => {
  switch (nodeType) {
    case 'host':
      return (<NodeTypeHost {...nodeProps} />)
    default:
      return (
        <>?????</>
      )
  }
}

export default NodeFactory