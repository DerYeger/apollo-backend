package eu.yeger.gramofo.fol.graph

import java.util.HashSet

class Graph {
    val vertexes: MutableSet<Vertex>
    val edges: MutableSet<Edge>

    /**
     * Ensures that references to other classes are bidirectional. If a new reference to another class
     * is set as some property, the other class should maybe reference this class too.
     */
    private fun addReferentialIntegrity() {
//        vertexes.addListener( (SetChangeListener<Vertex>) change -> {
//            if (change.wasRemoved()) {
//                change.getElementRemoved().setGraph( null );
//            }
//            if (change.wasAdded()) {
//                change.getElementAdded().setGraph( this );
//            }
//        });
//
//        edges.addListener( (SetChangeListener<Edge>) change -> {
//            if (change.wasRemoved()) {
//                change.getElementRemoved().setGraph( null );
//            }
//            if (change.wasAdded()) {
//                change.getElementAdded().setGraph( this );
//            }
//        });
    }

    init {
        vertexes = HashSet()
        edges = HashSet()
        addReferentialIntegrity()
    }
}
