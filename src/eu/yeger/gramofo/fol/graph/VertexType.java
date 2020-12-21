package eu.yeger.gramofo.fol.graph;


public enum VertexType {

    Circle   ("VertexType0"),
    Box      ("VertexType1"),
    Pentagon ("VertexType2"),
    Triangle ("VertexType3"),
    Hexagon  ("VertexType4"),
    Star     ("VertexType5");

    private final String name;

    VertexType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
