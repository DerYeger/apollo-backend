package eu.yeger.gramofo.fol.graph;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a edge in a typical graph.
 */
@SuppressWarnings("unused")
public class Edge implements Serializable {

    public static int idCounter = 0;

    private static int generateID() {
        int value = idCounter;
        idCounter = value + 1;
        return value;
    }

    private int id;
    private Vertex fromVertex;
    private Vertex toVertex;
    private List<String> stringAttachments;
    private transient Graph graph;

    public final Vertex dummy;

    public Edge() {
        this.id = generateID();
        this.fromVertex = null;
        this.stringAttachments = new ArrayList<>();
        this.graph = null;

        addReferentialIntegrity();

        dummy = new Vertex(false);
    }

    /**
     * Ensures that references to other classes are bidirectional. If a new reference to another class
     * is set as some property, the other class should maybe reference this class too.
     */
    private void addReferentialIntegrity() {

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

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.graph = null;
        addReferentialIntegrity();
    }

    ///////////////////////// operation and information //////////////////////

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vertex getFromVertex() {
        return fromVertex == null ? dummy : fromVertex;
    }

    public void setFromVertex(Vertex fromVertex) {
        this.fromVertex = fromVertex;
    }

    public Vertex getToVertex() {
        return toVertex == null ? dummy : toVertex;
    }

    public void setToVertex(Vertex toVertex) {
        this.toVertex = toVertex;
    }

    public Optional<Graph> getGraph() {
        return Optional.ofNullable(graph);
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public List<String> getStringAttachments() {
        return stringAttachments;
    }

    public void setStringAttachments(List<String> stringAttachments) {
        this.stringAttachments = stringAttachments;
    }
}
