package shaclbuilder;

import modelbuilder.ClassFinder;

import java.util.HashMap;
import java.util.Map;

public interface Shaclbuilder {

    public void build();

    public void buildNonKeys(HashMap<String, String> prefixes, Map<String, ClassFinder.rdfClass> classes);


}
