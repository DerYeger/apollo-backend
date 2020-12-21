package eu.yeger.gramofo.fol.graph

import java.io.Serializable
import java.util.*
import kotlin.jvm.JvmOverloads

class Vertex @JvmOverloads constructor(generateID: Boolean = true) : Serializable {
    ///////////////////////////// getter and setter //////////////////////////
    var id: Int = if (generateID) generateID() else -1
    var outgoingEdges: Set<Edge> = HashSet()
    var incomingEdges: Set<Edge> = HashSet()

    @Transient
    private var graph: Graph?
    var stringAttachments: List<String>
    var readableName: String

    /**
     * Ensures that references to other classes are bidirectional. If a new reference to another class
     * is set as some property, the other class should maybe reference this class too.
     */
    private fun addReferentialIntegrity() {
//        outgoingEdges.addListener((SetChangeListener<Edge>) change -> {
//            if (change.wasRemoved()) {
//                change.getElementRemoved().setFromVertex(null);
//            }
//            if (change.wasAdded()) {
//                change.getElementAdded().setFromVertex(this);
//            }
//        });
//
//        incomingEdges.addListener((SetChangeListener<Edge>) change -> {
//            if (change.wasRemoved()) {
//                change.getElementRemoved().setToVertex(null);
//            }
//            if (change.wasAdded()) {
//                change.getElementAdded().setToVertex(this);
//            }
//        });
//
//        graph.addListener((observable, oldValue, newValue) -> {
//            if (oldValue != null) {
//                oldValue.getVertexes().remove(this);
//            }
//            if (newValue != null) {
//                newValue.getVertexes().add(this);
//            }
//        });
    }
    ///////////////////////// operations //////////////////////
    /**
     * @param vertex the edge to check connectivity for
     * @return true, if this vertex has an outgoing edge to the given vertex.
     */
    fun isConnectedTo(vertex: Vertex): Boolean {
        for (edge in outgoingEdges) {
            if (edge.toVertex === vertex) {
                return true
            }
        }
        return false
    }

    fun getGraph(): Optional<Graph> {
        return Optional.ofNullable(graph)
    }

    fun setGraph(graph: Graph?) {
        this.graph = graph
    }

    override fun toString(): String {
        return "Vertex{" +
                "id=" + id +
                ", stringAttachments=" + stringAttachments +
                '}'
    }

    companion object {
        var idCounter = 0
        private fun generateID(): Int {
            val value = idCounter
            idCounter = value + 1
            return value
        }
    }

    init {
        graph = null
        stringAttachments = ArrayList()
        readableName = id.toString()
        addReferentialIntegrity()
    }
}