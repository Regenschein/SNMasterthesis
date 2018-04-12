package shaclbuilder;

import controller.Controller;
import modelbuilder.ClassFinder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.spin.util.JenaUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ShaclbuilderImplementation implements Shaclbuilder {

    public void build(){

        //buildExample();

        Model shapesmodel = JenaUtil.createDefaultModel();
        shapesmodel.read("./src/main/resources/eval/example-test-shape.ttl");
        //shapesmodel.read("./src/main/resources/data/hotelratings.shapes.ttl");

        Model datamodel = JenaUtil.createDefaultModel();
        //datamodel.read("./src/main/resources/eval/sparql-test-data.ttl");
        datamodel.read("./src/main/resources/data/world-0.1.ttl");

        Resource report = ValidationUtil.validateModel(datamodel, shapesmodel, false);
        //Resource report = ValidationUtil.validateModel(shapesmodel, shapesmodel, true);

        System.out.println(ModelPrinter.get().print(report.getModel()));

        Controller.getInstance().tA_main.setText(ModelPrinter.get().print(report.getModel()));

    }

    private void buildNodeShape(String shapeName, String targetClass, String[] properties) {
        StringBuilder sb = new StringBuilder();
        sb.append(shapeName + "\n");
        sb.append("\t a sh:NodeShape ;\n");
        sb.append("\t sh:targetClass " + targetClass + " ;\n");
        sb.append("\t sh:property [\n");
        for (String s : properties) {
            if(!s.equals("a")){
                sb.append("\t\t sh:path " + s + ";\n");
                sb.append("\t\t sh:maxCount 1 ;\n");
                sb.append("\t ] ;");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(".");
        System.out.println(sb.toString());
    }

    @Override
    public void buildNonKeys(HashMap<String, String> prefixes, Map<String, ClassFinder.rdfClass> classes){
        Iterator it = prefixes.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            sb.append("@prefix " + pair.getKey() + ": " + pair.getValue() + "> .\n");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        sb.append("@prefix ex: <https://example.org/> .\n");
        sb.append("@prefix sh: <http://www.w3.org/ns/shacl#> .\n");
        sb.append("\n");
        System.out.println(sb.toString());

        it = classes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry par = (Map.Entry)it.next();

            sb.append("ex:MaxNonKeyFor" + replace(prefixes, par.getKey().toString()) + "\n");
            sb.append("\t a sh:NodeShape ;" + "\n");
            sb.append("\t sh:targetClass " + par.getKey() + " ;\n");
            ClassFinder.rdfClass rdf = (ClassFinder.rdfClass) par.getValue();
            for (String att : rdf.getNonKeys()){
                if(!att.equals("a")) {
                    sb.append("\t sh:property [" + "\n");
                    sb.append("\t\t sh:path " + replace(prefixes, att) + " ;" + "\n");
                    sb.append("\t\t sh:minCount 1 ;" + "\n");
                    sb.append("\t ] ;" + "\n");
                }
            }
            //sb.deleteCharAt(sb.length() -3);
            sb.replace(sb.length() -3, sb.length(), ".\n\n");
        }


        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("./src/main/resources/eval/example-test-shape.ttl"));
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        build();

    }

    private String replace(HashMap<String, String> prefixes, String s){
        Iterator it = prefixes.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(s.contains(pair.getValue().toString())){
                return (pair.getKey().toString() + ":" + s.replace(pair.getValue().toString(), "").replace(">", ""));
            } else {
                return s;
            }
        }
        return "";
    }

    private void buildExample(){
        Set<String> prefixes = new HashSet<String>();
        prefixes.add("dash: <http://datashapes.org/dash#>");
        prefixes.add("rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        prefixes.add("rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
        prefixes.add("schema: <http://schema.org/>");
        prefixes.add("sh: <http://www.w3.org/ns/shacl#>");
        prefixes.add("xsd: <http://www.w3.org/2001/XMLSchema#>");
        prefixes.add("ex: <http://www.example.org#>");
        prefixes.add("owl: <http://www.w3.org/2002/07/owl#>");
        String s = buildPrefixes(prefixes);
        String[] prefixesArray = {"ex"};
        s = s + "\n\n" + buildSparqlShaclKey("ex:Exmaple", "ex:Person", "ex:name", prefixesArray);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("./src/main/resources/eval/example-test-shape.ttl"));
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildPrefixes(Set<String> prefixes){
        StringBuilder sb = new StringBuilder();
        for (String s : prefixes){
            sb.append("@prefix " + s + " .\n");
        }

        return sb.toString();
    }

    private String buildSparqlShaclKey(String shapeName, String targetClass, String key, String[] prefixes) {
        StringBuilder sb = new StringBuilder();
        sb.append(shapeName + "\n");
        sb.append("\t a sh:PropertyShape ;\n");
        sb.append("\t sh:targetClass " + targetClass + " ;\n");
        sb.append("\t sh:path " + key + " ;\n");
        sb.append("\t sh:sparql [\n");
        sb.append("\t\t a sh:SPARQLConstraint ; \n");
        sb.append("\t\t sh:message \"Values are " + key + "\" ;\n");
        sb.append("\t\t sh:prefixes ");
        for (String s : prefixes){
            sb.append(s + ": ");
        }
        sb.append(";\n");
        sb.append("\t\t sh:select \"\"\" \n");
        sb.append("\t\t\t SELECT ?this ?value \n");
        sb.append("\t\t\t WHERE {\n");
        sb.append("\t\t\t\t $this $PATH ?value .\n");
        sb.append("\t\t\t\t $that $PATH ?value .\n");
        sb.append("\t\t\t\t FILTER (?that != $this)\n");
        sb.append("\t\t\t } \n");
        sb.append("\t\t\t \"\"\" ;\n");
        sb.append("\t ] .\n");
        return sb.toString();

    }

    private void buildPropertyShape() {

    }

    private void buildKeyNode(){
        buildNodeShape("schema:PersonShape", "schema:Person", new String[]{"schema:\"given name\""});
    }

}
