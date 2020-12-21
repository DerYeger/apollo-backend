package eu.yeger.gramofo.fol.graph

import java.io.IOException
import java.io.ObjectInputStream
import java.io.Serializable
import java.lang.ClassNotFoundException
import java.util.*
import kotlin.Throws

/**
 * This class represents a edge in a typical graph.
 */
class Edge : Serializable {
    // /////////////////////// operation and information //////////////////////
    var id: Int = generateID()
    var fromVertex: Vertex?
        get() = if (field == null) dummy else field
    var toVertex: Vertex? = null
        get() = if (field == null) dummy else field
    var stringAttachments: List<String>

    @Transient
    private var graph: Graph?
    private val dummy: Vertex = Vertex(false)

    /**
     * Ensures that references to other classes are bidirectional. If a new reference to another class
     * is set as some property, the other class should maybe reference this class too.
     */
    private fun addReferentialIntegrity() {

//        fromVertex.addListener( (observable, oldValue, newValue ) -> {
//            if (oldValue != null){
//               oldValue.getOutgoingEdges().remove(this);
//            }
//            if (newValue != null){
//                newValue.getOutgoingEdges().add(this);
//            }
//        });
//
//        toVertex.addListener( (observable, oldValue, newValue ) -> {
//            if (oldValue != null){
//                oldValue.getIncomingEdges().remove(this);
//            }
//            if (newValue != null){
//                newValue.getIncomingEdges().add(this);
//            }
//        });
//
//        graph.addListener( (observable, oldValue, newValue) -> {
//            if (oldValue != null){
//                oldValue.getEdges().remove(this);
//            }
//            if (newValue != null){
//                newValue.getEdges().add(this);
//            }
//        });
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(ois: ObjectInputStream) {
        ois.defaultReadObject()
        graph = null
        addReferentialIntegrity()
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
        fromVertex = null
        stringAttachments = ArrayList()
        graph = null
        addReferentialIntegrity()
    }
}
