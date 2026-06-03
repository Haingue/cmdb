import type { NodeProps } from '@xyflow/react'
import BusinessServiceNodeType from './node-types/BusinessServiceNodeType'
import DefaultNodeType from './node-types/DefaultNodeType'
import ProjectNodeType from './node-types/ProjectNodeType'
import HostNodeType from './node-types/HostNodeType'
import SoftwareNodeType from './node-types/SoftwareNodeType'
import EnvironmentNodeType from './node-types/EnvironmentNodeType'

const NodeTypeFactory = {
    "VirtualMachine": HostNodeType,
    "Vlan": DefaultNodeType,
    "Project": ProjectNodeType,
    "Traffic": DefaultNodeType,
    "Hardware": HostNodeType,
    "Environment": EnvironmentNodeType,
    "BusinessService": BusinessServiceNodeType,
    "Software": SoftwareNodeType,
    "Component": DefaultNodeType,
    "Host": HostNodeType,
    "default": DefaultNodeType
}

export default NodeTypeFactory