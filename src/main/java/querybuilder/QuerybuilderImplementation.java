package querybuilder;

import controller.Configuration;
import controller.Controller;
import model.BuilderImplementation;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.util.ModelPrinter;

import java.util.*;

public class QuerybuilderImplementation extends BuilderImplementation implements Querybuilder{


    public void build(HashMap<String, String> prefixes, Set<String> key, String rdfsClass, int almostKey) {
        OntModel m = getModel();
        loadData( m );

        StringBuilder prefixSB = new StringBuilder();

        for(Map.Entry<String, String> entry : prefixes.entrySet()){
            String shortcut = entry.getKey();
            String uri = entry.getValue();
            prefixSB.append("prefix " + shortcut + ": " + uri + "> \n");
        }

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
        query.append(" ?count \n");
        query.append("  WHERE { \n");
        query.append("    {\n ");
        query.append("      SELECT ");
        for (String v : variables){
            query.append(" ?" + v);
        }
        query.append(" (count(?" + variables.iterator().next() + ") as ?count)");
        query.append(" \n      WHERE { \n");
        query.append("         $this a " + buildShort(prefixes, rdfsClass) + ". \n");
        for(Map.Entry<String, String> entry : mapping.entrySet()){
            String variable = entry.getKey();
            String keyFragment = entry.getValue();
            if(variable.equals("a")){
                keyFragment = "a";
            }
            query.append("          ?this " + keyFragment + " ?" + variable + " .\n");
        }
        query.append("        } \n");
        query.append("        GROUP BY ");
        for (String v : variables){
            query.append(" ?" + v);
        }
        query.append("\n      }\n");
        query.append("    FILTER (?count > " + almostKey + ")");
        query.append("  }\n\n");

        String qwery = query.toString();

        Controller.getInstance().tA_main.appendText(qwery);

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
        try {
            Query query = QueryFactory.create(q);
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
        } catch (QueryParseException qpe){
            System.out.println("Bumpedibu");
        }
    }

    private void print(Object s){
        System.out.println(s);
    }

    @Override
    public void build() {

    }

}
