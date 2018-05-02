package querybuilder;

import controller.Configuration;
import model.BuilderImplementation;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

import java.util.*;

public class QuerybuilderImplementation extends BuilderImplementation implements Querybuilder{


    public void build2() {
        OntModel m = getModel();
        loadData( m );
        String prefix = "prefix world: <" + WORLD_NS + ">\n" +
                "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                "prefix owl: <" + OWL.getURI() + ">\n" +
                "prefix schema: <http://data.masterthesis.com/schema/world/>\n";

        // (count(?value) as ?count)
        //                    "   FILTER (?this != ?that) . " +
        // +
        //                    "GROUP BY ?region ?country")
        //+
        //                 "HAVING (?count > 1)" + ""
        showQuery( m,
                prefix +
                        "SELECT ?region ?country ?count " +
                        "WHERE {" +
                        "{" +
                        "SELECT ?region ?country (count(?region) as ?count)" +
                        "      WHERE {" +
                        "        $this a schema:Place . " +
                        "        $this schema:addressRegion ?region . " +
                        "        $this schema:addressCountry ?country . " +
                        "      }" +
                        "      GROUP BY ?region ?country" +
                        "}" +
                        "FILTER (?count > 1)" +
                        "}");
    }

    public void build(HashMap<String, String> prefixes, Set<String> key, String rdfsClass, int almostKey) {
        OntModel m = getModel();
        loadData( m );

        StringBuilder prefixSB = new StringBuilder();

        for(Map.Entry<String, String> entry : prefixes.entrySet()){
            String shortcut = entry.getKey();
            String uri = entry.getValue();
            prefixSB.append("prefix " + shortcut + ": " + uri + "> \n");
        }

        print("bp");

        String prefix = prefixSB.toString();
        Set<String> variables = new HashSet<String>();
        Set<String> keyFragments = new HashSet<String>();
        HashMap<String, String> mapping = new HashMap<String, String>();
        for (String s : key) {
            variables.add(trimToName(s));
            keyFragments.add(buildShort(prefixes, s));
            mapping.put(trimToName(s), buildShort(prefixes, s));
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT");
        for (String v : variables){
            query.append(" ?" + v);
        }
        query.append(" ?count ");
        query.append(" WHERE { ");
        query.append(" { ");
        query.append(" SELECT ");
        for (String v : variables){
            query.append(" ?" + v);
        }
        query.append(" (count(?" + variables.iterator().next() + ") as ?count)");
        query.append(" WHERE { ");
        query.append(" $this a " + buildShort(prefixes, rdfsClass) + ". "); //TODO: CONTINUE HERE DOPPELPUNKT IS MISSING
        for(Map.Entry<String, String> entry : mapping.entrySet()){
            String variable = entry.getKey();
            String keyFragment = entry.getValue();
            query.append("?this " + keyFragment + " ?" + variable + " .");
        }
        query.append("} ");
        query.append("GROUP BY ");
        for (String v : variables){
            query.append(" ?" + v);
        }
        query.append("}");
        query.append(" FILTER (?count > " + almostKey + ")");
        query.append("}");

        String qwery = query.toString();

        showQuery(m, prefixSB.toString() + query.toString());

        /*
        showQuery( m,
                prefix +
                    "SELECT ?region ?country ?count " +
                    "WHERE {" +
                    "{" +
                    "SELECT ?region ?country (count(?region) as ?count)" +
                    "      WHERE {" +
                    "        $this a schema:Place . " +
                    "        $this schema:addressRegion ?region . " +
                    "        $this schema:addressCountry ?country . " +
                    "      }" +
                    "      GROUP BY ?region ?country" +
                    "}" +
                    "FILTER (?count > 1)" +
                    "}");
       */
    }

    public void buildOld() {
        OntModel m = getModel();
        loadData( m );
        String prefix = "prefix world: <" + WORLD_NS + ">\n" +
                "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                "prefix owl: <" + OWL.getURI() + ">\n";

        showQuery( m,
                prefix +
                        "SELECT ?value (count(?value) as ?count) " +
                        "WHERE {" +
                        "    $this $PATH ?value . " +
                        "    $that $PATH ?value . " +
                        "   FILTER (?this != ?that) . " +
                        "}" +
                        "GROUP BY ?value");
    }

    public void buildback() {
        OntModel m = getModel();
        loadData( m );
        String prefix = "prefix world: <" + WORLD_NS + ">\n" +
                "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                "prefix owl: <" + OWL.getURI() + ">\n";

        showQuery( m,
                prefix +
                        "SELECT ?value (count(distinct ?value) as ?count) " +
                        "WHERE {" +
                        "    $this $PATH ?value . " +
                        "            FILTER (?value != \"torben\") " +
                        "}" +
                        "GROUP BY ?value");
    }

    protected OntModel getModel() {
        return ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
    }

    protected void loadData( Model m ) {
        FileManager.get().readModel( m, Configuration.getInstance().getPath());
    }

    protected void showQuery( Model m, String q ) {
        Query query = QueryFactory.create( q );
        QueryExecution qexec = QueryExecutionFactory.create( query, m );
        try {
            ResultSet results = qexec.execSelect();
            print("Key-Candidate is no key because there are mulitple results:");
            String s = ResultSetFormatter.asText(results);
            System.out.println(s);
        } catch(Exception e){

        }
        finally {
            qexec.close();
        }

    }

    private void print(Object s){
        System.out.println(s);
    }

    @Override
    public void build() {

    }

}
