import type { NodeProps } from '@xyflow/react'
import BusinessServiceNodeType from './node-types/BusinessServiceNodeType'
import DefaultNodeType from './node-types/DefaultNodeType'
import ProjectNodeType from './node-types/ProjectNodeType'
import HostNodeType from './node-types/HostNodeType'
import SoftwareNodeType from './node-types/SoftwareNodeType'
import EnvironmentNodeType from './node-types/EnvironmentNodeType'

const NodeFactory = (nodeType: string, nodeProps :NodeProps) => {
  switch (nodeType) {
    case 'VirtualMachine':
      return (<HostNodeType {...nodeProps} />)
    case 'Vlan':
      return (<DefaultNodeType {...nodeProps} />)
    case 'Project':
      return (<ProjectNodeType {...nodeProps} />)
    case 'Traffic':
      return (<DefaultNodeType {...nodeProps} />)
    case 'Hardware':
      return (<HostNodeType {...nodeProps} />)
    case 'Environment':
      return (<EnvironmentNodeType {...nodeProps} />)
    case 'BusinessService':
      return (<BusinessServiceNodeType {...nodeProps} />)
    case 'Software':
      return (<SoftwareNodeType {...nodeProps} />)
    case 'Component':
      return (<DefaultNodeType {...nodeProps} />)
    case 'Host':
      return (<HostNodeType {...nodeProps} />)
    default:
      return (
        (<DefaultNodeType {...nodeProps} />)
      )
  }
}

export default NodeFactory