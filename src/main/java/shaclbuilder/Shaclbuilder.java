package shaclbuilder;

import modelbuilder.RdfClass;
import org.apache.jena.rdf.model.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Shaclbuilder {

    public void build();

    public void buildWithName(String s);

    public void build(String p);

    public void buildNonKeys(HashMap<String, String> prefixes, Map<String, RdfClass> classes);

    void buildConditionalASKKeys(Model model, Map<String, RdfClass> classes);

    public void build(HashMap<String, String> prefixes, Set<String> amk, String name, int almostKey);

    public void buildNonKeys(Model model, Map<String, RdfClass> classes);

    public void buildAlmostKeys(Model model, Map<String, RdfClass> classes);

    int buildConditionalKeys(Model model, Map<String, RdfClass> classes);

    void buildAlmostASKKeys(Model model, Map<String, RdfClass> classes);

    void buildFunctionalDependencie(Model model, Map<String, RdfClass> classes);
}
