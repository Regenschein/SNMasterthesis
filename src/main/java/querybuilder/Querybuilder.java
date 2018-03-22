package querybuilder;

public interface Querybuilder {

    public static final String SOURCE = "./src/main/resources/data/";

    // Pizza ontology namespace
    //public static final String PIZZA_NS = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
    public static final String WORLD_NS = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";

    public void build();

}
