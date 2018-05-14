package shaclbuilder;

import controller.Configuration;
import controller.Controller;
import model.BuilderImplementation;
import modelbuilder.ClassFinder;
import modelbuilder.RdfClass;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ShaclbuilderImplementation extends BuilderImplementation implements Shaclbuilder {

    public void build(){
        Model shapesmodel = JenaUtil.createDefaultModel();
        shapesmodel.read(Configuration.getInstance().getShaclpath());

        Model datamodel = JenaUtil.createDefaultModel();
        datamodel.read(Configuration.getInstance().getPath());

        Resource report = ValidationUtil.validateModel(datamodel, shapesmodel, false);

        System.out.println(ModelPrinter.get().print(report.getModel()));

        Controller.getInstance().tA_main.appendText(ModelPrinter.get().print(report.getModel()));

    }

    @Override
    public void buildNonKeys(HashMap<String, String> prefixes, Map<String, RdfClass> classes) {

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
    public void buildNonKeys(Model model, Map<String, RdfClass> classes){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> pair : model.getNsPrefixMap().entrySet()) {
            System.out.println(pair.getKey() + " = " + pair.getValue());
            sb.append("@prefix " + pair.getKey() + ": <" + pair.getValue() + "> .\n");
        }
        sb.append("@prefix ex: <https://example.org/> .\n");
        sb.append("@prefix sh: <http://www.w3.org/ns/shacl#> .\n");
        sb.append("\n");
        System.out.println(sb.toString());

        Iterator it = classes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry par = (Map.Entry)it.next();

            String targetClass = par.getKey().toString();

            sb.append("ex:MaxNonKeyFor" + targetClass + "\n");
            sb.append("\t a sh:NodeShape ;" + "\n");
            sb.append("\t sh:targetClass " + targetClass + " ;\n");
            RdfClass rdf = (RdfClass) par.getValue();

            if (Controller.getInstance().checkBox_AlmostKey.isSelected()) {
                Set<Set<String>> currentSet = rdf.getAlmostKeys();
            }

            if(rdf.getAlmostKeys().size() > 1){
                sb.append("\t sh:or ( \n");
                for (Set<String> set : rdf.getAlmostKeys()){
                    sb.append("\t\t [ \n");
                    sb.append("\t\t\t sh:and ( \n");
                    for (String att : set){
                        if(!att.equals("a")) {
                            sb.append("\t\t\t\t [" + "\n");
                            sb.append("\t\t\t\t\t sh:path " + trimAttr(att) + " ;" + "\n");
                            sb.append("\t\t\t\t\t sh:minCount 1 ;" + "\n");
                            sb.append("\t\t\t\t ] \n");
                        }
                    }
                    sb.append("\t\t\t ) \n");
                    sb.append("\t\t ] \n");
                }
                sb.append("\t ) .\n");
            } else {
                for (Set<String> set : rdf.getAlmostKeys()){
                    for (String att : set){
                        if(!att.equals("a")) {
                            sb.append("\t sh:property [" + "\n");
                            sb.append("\t\t sh:path " + trimAttr(att) + " ;" + "\n");
                            sb.append("\t\t sh:minCount 1 ;" + "\n");
                            sb.append("\t ] ;" + "\n");
                        }
                    }
                }
                sb.replace(sb.length() -3, sb.length(), ".\n\n");
            }

        }



        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(Configuration.getInstance().getShaclpath()));
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //build();

    }

    private String trimAttr(String att){
        if (att.equals("a")){
            return att;
        } else {
            return att.split("%§%")[1];
        }
    }

    @Override
    public void build(HashMap<String, String> prefixes, Set<String> amk, String name, int almostKey) {

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

    private boolean stringIsURI(String s){
        if(s.matches("<?\"?http://.*>?\"?")){
            return true;
        }
        return false;
    }

}
