import CommunicatesWithEdge from './edge-types/CommunicatesWithEdge'
import ComposedOfEdge from './edge-types/ComposedOfEdge'
import TrafficEdge from './edge-types/TrafficEdge'
import DefaultNodeType from './node-types/DefaultNodeType'

const EdgeTypeFactory = {
    "traffic": TrafficEdge,
    "Communicate with": CommunicatesWithEdge,
    "Composed of": ComposedOfEdge,
    "default": DefaultNodeType
}

export default EdgeTypeFactory