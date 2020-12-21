package eu.yeger.gramofo.fol.graph;

import java.io.Serializable;
import java.util.*;

public class Vertex implements Serializable {

    public static int idCounter = 0;

    private static int generateID() {
        int value = idCounter;
        idCounter = value + 1;
        return value;
    }

    private int id;
    private VertexType type;
    private Set<Edge> outgoingEdges;
    private Set<Edge> incomingEdges;
    private transient Graph graph;
    private List<String> stringAttachments;

    private String readableName;

    public Vertex() {
        this(VertexType.Circle, true);
    }

    public Vertex(boolean generateID) {
        this(VertexType.Circle, generateID);
    }

    public Vertex(VertexType type, boolean generateID) {

        this.id = generateID ? generateID() : -1;
        this.type = type;
        this.outgoingEdges = new HashSet<>();
        this.incomingEdges = new HashSet<>();
        this.graph = null;
        this.stringAttachments = new ArrayList<>();
        this.readableName = String.valueOf(this.id);

        addReferentialIntegrity();
    }

    /**
     * Ensures that references to other classes are bidirectional. If a new reference to another class
     * is set as some property, the other class should maybe reference this class too.
     */
    private void addReferentialIntegrity() {
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
    public boolean isConnectedTo(Vertex vertex) {
        for (Edge edge : outgoingEdges) {
            if (edge.getToVertex() == vertex) {
                return true;
            }
        }

        return false;
    }

    ///////////////////////////// getter and setter //////////////////////////

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public VertexType getType() {
        return type;
    }
    public void setType(VertexType type) {
        this.type = type;
    }


    public Set<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }
    public void setOutgoingEdges(Set<Edge> outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }

    public Set<Edge> getIncomingEdges() {
        return incomingEdges;
    }
    public void setIncomingEdges(Set<Edge> incomingEdges) {
        this.incomingEdges = incomingEdges;
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

    public String getReadableName() {
        return readableName;
    }
    public void setReadableName(String readableName) {
        this.readableName = readableName;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "id=" + id +
                ", stringAttachments=" + stringAttachments +
                '}';
    }
}
