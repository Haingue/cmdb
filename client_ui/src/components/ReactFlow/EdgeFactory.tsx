import CommunicatesWithEdge from './edge-types/CommunicatesWithEdge'
import ComposedOfEdge from './edge-types/ComposedOfEdge'
import DefaultEdge from './edge-types/DefaultEdge'
import TrafficEdge from './edge-types/TrafficEdge'
import DefaultNodeType from './node-types/DefaultNodeType'

const EdgeTypeFactory = {
    "traffic": TrafficEdge,
    "Communicate with": CommunicatesWithEdge,
    "Composed of": ComposedOfEdge,
    "Implemented by": ComposedOfEdge,
    "default": DefaultEdge
}

export default EdgeTypeFactory