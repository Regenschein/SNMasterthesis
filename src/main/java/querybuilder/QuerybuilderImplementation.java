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

        //for(Map.Entry<String, String> entry : prefixes.entrySet()){
        for(Map.Entry<String, String> entry : m.getNsPrefixMap().entrySet()){
            prefixSB.append("prefix " + entry.getKey() + ": <" + entry.getValue() + ">\n");
        }



        String prefix = prefixSB.toString();
        Set<String> variables = new HashSet<String>();
        Set<String> keyFragments = new HashSet<String>();
        HashMap<String, String> mapping = new HashMap<String, String>();
        for (String s : key) {
            variables.add(trimToName(s));
            keyFragments.add(buildShort(prefixes, s));
            mapping.put(trimTo(s), buildShort(prefixes, s));
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT");
        for (String v : variables){
            query.append(" ?" + v.replace(":", "0")); //we can't use : in querynames so we just skip them :^)
        }
        query.append(" ?count \n");
        query.append("  WHERE { \n");
        query.append("    {\n ");
        query.append("      SELECT ");
        for (String v : variables){
            query.append(" ?" + v.replace(":", "0")); //we can't use : in querynames so we just skip them :^)
        }
        query.append(" (count(?" + variables.iterator().next().replace(":", "0") + ") as ?count)"); //we can't use : in querynames so we just skip them :^)
        query.append(" \n      WHERE { \n");
        //query.append("         $this a " + buildShort(prefixes, rdfsClass) + ". \n");
        query.append("         $this a " + rdfsClass + ". \n");
        for(Map.Entry<String, String> entry : mapping.entrySet()){
            String variable = entry.getKey();
            String keyFragment = entry.getValue();
            if(variable.equals("a")){
                keyFragment = "a";
            }
            //query.append("          ?this " + variable + " ?" + variable.replace(":", "0")  + " .\n");
            query.append("          ?this " + trimVariableName(variable) + " ?" + trimToName(variable)  + " .\n");
        }
        query.append("        } \n");
        query.append("        GROUP BY ");
        for (String v : variables){
            query.append(" ?" + v.replace(":", "0"));
        }
        query.append("\n      }\n");
        query.append("    FILTER (?count > " + almostKey + ")");
        query.append("  }\n\n");

        String qwery = query.toString();

        Controller.getInstance().tA_main.appendText(qwery);

        showQuery(m, prefixSB.toString() + query.toString());
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
            qpe.printStackTrace();
        }
    }

    private void print(Object s){
        System.out.println(s);
    }

    @Override
    public void build() {
        OntModel m = getModel();
        loadData( m );

        StringBuilder prefixSB = new StringBuilder();

        prefixSB.append("prefix world: <http://data.masterthesis.com/dataset/world/>\n");
        prefixSB.append("prefix schema: <http://data.masterthesis.com/schema/world/>\n");
        prefixSB.append("prefix : <http://data.masterthesis.com/dataset/world/>\n");

        //for(Map.Entry<String, String> entry : prefixes.entrySet()){
        for(Map.Entry<String, String> entry : m.getNsPrefixMap().entrySet()){
            prefixSB.append("prefix " + entry.getKey() + ": <" + entry.getValue() + ">\n");
        }

        StringBuilder query = new StringBuilder();
        query.append(
            //"   SELECT  $this ?schema0addressCountry ?schema0isCapital (count(?schema0addressCountry) as ?count)  \n" +
            "   SELECT  $this \n" +
            "   WHERE {  \n" +
            "       $this a schema:City.  \n" +
            "       ?this schema:isCapital ?schema0isCapital .  \n" +
            "       ?this schema:addressCountry ?schema0addressCountry .  \n" +
            "       ?that schema:addressCountry ?schema0addressCountry .  \n" +
            "       ?that schema:isCapital ?schema0isCapital .  \n" +
            "       $that a schema:City.  \n" +
            "       FILTER (?that != ?this) \n" +
            "   } GROUP BY $this"
            //"   } GROUP BY $this ?schema0addressCountry ?schema0isCapital"
         );

        String qwery = query.toString();

        Controller.getInstance().tA_main.appendText(qwery);

        showQuery(m, prefixSB.toString() + query.toString());
    }

    public void build2() {
        OntModel m = getModel();
        loadData( m );

        StringBuilder prefixSB = new StringBuilder();

        prefixSB.append("prefix world: <http://data.masterthesis.com/dataset/world/>\n");
        prefixSB.append("prefix schema: <http://data.masterthesis.com/schema/world/>\n");
        prefixSB.append("prefix : <http://data.masterthesis.com/dataset/world/>\n");

        //for(Map.Entry<String, String> entry : prefixes.entrySet()){
        for(Map.Entry<String, String> entry : m.getNsPrefixMap().entrySet()){
            prefixSB.append("prefix " + entry.getKey() + ": <" + entry.getValue() + ">\n");
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT ?class ?target ?percentage \n" +
                "WHERE { \n" +
                "   ?class a schema:Person .\n" +
                "   ?class schema:social_Rank _:bN0 .\n" +
                "   _:bN0 schema:socialRankValue ?percentage .\n" +
                "   _:bN0 schema:socialRankTarget ?target .\n" +
                "FILTER (?percentage > 95) \n" +
                "} \n" +
                "ORDER BY ?name \n"
                );

        String qwery = query.toString();

        Controller.getInstance().tA_main.appendText(qwery);

        showQuery(m, prefixSB.toString() + query.toString());
    }

    private String trimVariableName(String var){
        if(!var.equals("a"))
            return var.split("%§%")[1];
        return var;
    }

}
