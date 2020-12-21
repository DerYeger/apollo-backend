package eu.yeger.gramofo.fol.graph;

import java.util.HashSet;
import java.util.Set;

public class Graph {

    private final Set<Vertex> vertexes;
	private final Set<Edge> edges;

	public Graph() {
		this.vertexes     = new HashSet<>();
        this.edges        = new HashSet<>();

        addReferentialIntegrity();
    }

    /**
     * Ensures that references to other classes are bidirectional. If a new reference to another class
     * is set as some property, the other class should maybe reference this class too.
     */
    private void addReferentialIntegrity() {
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

    public Set<Vertex> getVertexes() {
        return vertexes;
    }

    public Set<Edge> getEdges() {
        return edges;
    }
}
