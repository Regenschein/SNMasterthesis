package querybuilder;

import controller.Controller;
import model.BuilderImplementation;
import modelbuilder.Modelreader;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.topbraid.jenax.util.JenaUtil;


import java.util.*;

public class QuerybuilderImplementation extends BuilderImplementation implements Querybuilder{

    public void build(HashMap<String, String> prefixes, Set<String> key, String rdfsClass, int almostKey) {
        StringBuilder prefixSB = new StringBuilder();

        Model m = Modelreader.getInstance().getModel();

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
                print(qexec.execAsk());
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
    public void buildClasses() {
        StringBuilder prefixSB = new StringBuilder();

        prefixSB.append("prefix world: <http://data.masterthesis.com/dataset/world/>\n");
        prefixSB.append("prefix schema: <http://data.masterthesis.com/schema/world/>\n");
        prefixSB.append("prefix : <http://data.masterthesis.com/dataset/world/>\n");

        Model m = Modelreader.getInstance().getModel();

        for(Map.Entry<String, String> entry : m.getNsPrefixMap().entrySet()){
            prefixSB.append("prefix " + entry.getKey() + ": <" + entry.getValue() + ">\n");
        }

        StringBuilder query = new StringBuilder();
        query.append(
                "   SELECT DISTINCT $obj \n" +
                        "   WHERE {   \n" +
                        "       $sub a $obj.} "
        );

        Query q = QueryFactory.create(query.toString());
        QueryExecution qexec = QueryExecutionFactory.create(q, m );
        ResultSet results = qexec.execSelect();

        while (results.hasNext()){
            QuerySolution querySolution = results.next();
            buildClassfile(m.getNsURIPrefix(querySolution.get("obj").asResource().getNameSpace()) + ":" + querySolution.get("obj").asResource().getLocalName());
        }
    }

    public void buildClassfile(String classname) {
        StringBuilder prefixSB = new StringBuilder();

        prefixSB.append("prefix world: <http://data.masterthesis.com/dataset/world/>\n");
        prefixSB.append("prefix schema: <http://data.masterthesis.com/schema/world/>\n");
        prefixSB.append("prefix : <http://data.masterthesis.com/dataset/world/>\n");

        Model m = Modelreader.getInstance().getModel();

        for(Map.Entry<String, String> entry : m.getNsPrefixMap().entrySet()){
            prefixSB.append("prefix " + entry.getKey() + ": <" + entry.getValue() + ">\n");
        }

        StringBuilder query = new StringBuilder();
        query.append(
                 "   SELECT  * \n" +
                 "   WHERE {   \n" +
                 "       $sub a " + classname + ".  \n" +
                 "       $sub $pred $obj } "
        );

        resultToModel(m, prefixSB.toString() + query.toString(), classname.replace(":", ""));
    }

    private void resultToModel( Model m, String q , String classname) {
        Model momo = JenaUtil.createDefaultModel();
        momo.setNsPrefixes(m.getNsPrefixMap());
        try {
            Query query = QueryFactory.create(q);
            QueryExecution qexec = QueryExecutionFactory.create( query, m );
            try {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()){
                    QuerySolution querySolution = results.next();
                    querySolution.get("sub");
                    if (momo.getProperty(String.valueOf(querySolution.get("pred"))) == null){
                        momo.createProperty(String.valueOf(querySolution.get("pred")));
                    }
                    momo.add(querySolution.getResource("sub"), momo.getProperty(String.valueOf(querySolution.get("pred"))), querySolution.get("obj"));
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            finally {
                qexec.close();
                Modelreader.getInstance().writeClassFile(classname, momo);
            }
        } catch (QueryParseException qpe){
            qpe.printStackTrace();
        }
    }

    private String trimVariableName(String var){
        if(!var.equals("a"))
            return var.split("%&%")[1];
        return var;
    }

}
