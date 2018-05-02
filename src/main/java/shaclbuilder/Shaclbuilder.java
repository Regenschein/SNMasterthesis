package shaclbuilder;

import modelbuilder.ClassFinder;
import modelbuilder.RdfClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Shaclbuilder {

    public void build();

    public void buildNonKeys(HashMap<String, String> prefixes, Map<String, RdfClass> classes);

    public void build(HashMap<String, String> prefixes, Set<String> amk, String name, int almostKey);
}
