package shaclbuilder;

import controller.Controller;
import model.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.spin.util.JenaUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ShaclbuilderImplementation implements Shaclbuilder {

    public void build(){

        buildExample();

        Model shapesmodel = JenaUtil.createDefaultModel();
        shapesmodel.read("./src/main/resources/eval/example-test-shape.ttl");
        //shapesmodel.read("./src/main/resources/data/hotelratings.shapes.ttl");

        Model datamodel = JenaUtil.createDefaultModel();
        datamodel.read("./src/main/resources/eval/sparql-test-data.ttl");

        Resource report = ValidationUtil.validateModel(datamodel, shapesmodel, false);
        //Resource report = ValidationUtil.validateModel(shapesmodel, shapesmodel, true);

        System.out.println(ModelPrinter.get().print(report.getModel()));

        Controller.getInstance().tA_main.setText(ModelPrinter.get().print(report.getModel()));

    }

    //ValidationExample

    private void buildNodeShape(String shapeName, String targetClass, String[] properties) {
        StringBuilder sb = new StringBuilder();
        sb.append(shapeName + "\n");
        sb.append("\t a sh:NodeShape ;\n");
        sb.append("\t sh:targetClass " + targetClass + " ;\n");
        sb.append("\t sh:property [\n");
        for (String s : properties) {
            sb.append("\t\t sh:path " + s + ";\n");
            sb.append("\t\t sh:maxCount 1 ;\n");
            sb.append("\t ] ;");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(".");
        System.out.println(sb.toString());

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
