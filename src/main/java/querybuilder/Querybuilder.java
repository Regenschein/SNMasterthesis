package querybuilder;

import java.util.HashMap;
import java.util.Set;

public interface Querybuilder {

    public static final String SOURCE = "./src/main/resources/data/";

    public static final String WORLD_NS = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";

    public void build();

    void build(HashMap<String, String> prefixes, Set<String> amk, String name, int almostKey);
}
