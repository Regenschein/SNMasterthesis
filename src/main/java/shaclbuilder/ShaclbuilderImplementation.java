package shaclbuilder;

import controller.Configuration;
import controller.Controller;
import model.BuilderImplementation;
import modelbuilder.ConditionalKey;
import modelbuilder.RdfClass;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ShaclbuilderImplementation extends BuilderImplementation implements Shaclbuilder {



    public void build(){
        Model shapesmodel = JenaUtil.createDefaultModel();
        try {
            shapesmodel.read(new FileInputStream(Configuration.getInstance().getShaclpath()), "urn:dummy", FileUtils.langTurtle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Model datamodel = JenaUtil.createDefaultModel();
        try {
            datamodel.read(new FileInputStream(Configuration.getInstance().getPath()), "urn:dummy", FileUtils.langTurtle);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(shapesmodel.size() + " " + datamodel.size());

        try {
            Resource report = ValidationUtil.validateModel(datamodel, shapesmodel, false);
            try {
                Controller.getInstance().tA_main.appendText(ModelPrinter.get().print(report.getModel()));
            }catch(NoClassDefFoundError | ExceptionInInitializerError exc){
                System.out.println(ModelPrinter.get().print(report.getModel()));
            }
            BufferedWriter writer = null;
            try {

                writer = Files.newBufferedWriter(java.nio.file.Paths.get(("evaluationresult" + System.currentTimeMillis() +".ttl")), StandardCharsets.UTF_8);
                writer.write(ModelPrinter.get().print(report.getModel()));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }catch (NullPointerException errr){
            errr.printStackTrace();
        }catch (ExceptionInInitializerError errr){
            errr.printStackTrace();
        }
    }


    public void buildWithName(String s){
        Model shapesmodel = JenaUtil.createDefaultModel();
        try {
            shapesmodel.read(new FileInputStream(Configuration.getInstance().getShaclpath()), "urn:dummy", FileUtils.langTurtle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Model datamodel = JenaUtil.createDefaultModel();
        try {
            datamodel.read(new FileInputStream(Configuration.getInstance().getPath()), "urn:dummy", FileUtils.langTurtle);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(shapesmodel.size() + " " + datamodel.size());

        try {
            Resource report = ValidationUtil.validateModel(datamodel, shapesmodel, false);
            try {
                Controller.getInstance().tA_main.appendText(ModelPrinter.get().print(report.getModel()));
            }catch(NoClassDefFoundError | ExceptionInInitializerError exc){
                System.out.println(ModelPrinter.get().print(report.getModel()));
            }
            BufferedWriter writer = null;
            try {

                writer = Files.newBufferedWriter(java.nio.file.Paths.get(("evaluationresult" + s +".ttl")), StandardCharsets.UTF_8);
                writer.write(ModelPrinter.get().print(report.getModel()));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }catch (NullPointerException errr){
            errr.printStackTrace();
        }catch (ExceptionInInitializerError errr){
            errr.printStackTrace();
        }
    }

    public void build(String p){
        Model shapesmodel = JenaUtil.createDefaultModel();
        try {
            shapesmodel.read(new FileInputStream(p), "urn:dummy", FileUtils.langTurtle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Model datamodel = JenaUtil.createDefaultModel();
        try {
            datamodel.read(new FileInputStream(Configuration.getInstance().getPath()), "urn:dummy", FileUtils.langTurtle);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(shapesmodel.size() + " " + datamodel.size());

        try {
            Resource report = ValidationUtil.validateModel(datamodel, shapesmodel, false);
            try {
                Controller.getInstance().tA_main.appendText(ModelPrinter.get().print(report.getModel()));
            } catch (NoClassDefFoundError | ExceptionInInitializerError exc) {
                System.out.println(ModelPrinter.get().print(report.getModel()));
            }
            BufferedWriter writer = null;
            try {
                writer = Files.newBufferedWriter(java.nio.file.Paths.get(("evaluationresult" + System.currentTimeMillis() + p.split("--")[1])), StandardCharsets.UTF_8);
                writer.write(ModelPrinter.get().print(report.getModel()));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (StackOverflowError soe){
            System.out.println("WHAT THE DUCK?");
            soe.printStackTrace();
            System.out.println("WHAT THE DUCK?");
        }catch (NullPointerException errr){
            errr.printStackTrace();
        }catch (ExceptionInInitializerError errr){
            errr.printStackTrace();
        }
    }

    @Override
    public void buildNonKeys(HashMap<String, String> prefixes, Map<String, RdfClass> classes) {

    }

    private StringBuilder buildPrefixe(Model model){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> pair : model.getNsPrefixMap().entrySet()) {
            System.out.println(pair.getKey() + " = " + pair.getValue());
            sb.append("@prefix " + pair.getKey() + ": <" + pair.getValue() + "> .\n");
        }
        sb.append("@prefix ex: <https://example.org/> .\n");
        sb.append("@prefix sh: <http://www.w3.org/ns/shacl#> .\n");
        sb.append("@prefix owl: <http://www.w3.org/2002/07/owl#> .\n");
        sb.append("@prefix ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>.\n");
        sb.append("\n");
        return sb;
    }

    @Override
    public void buildNonKeys(Model model, Map<String, RdfClass> classes){
        StringBuilder sb = buildPrefixe(model);

        Iterator it = classes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry par = (Map.Entry)it.next();

            String targetClass = par.getKey().toString();

            sb.append("ex:MaxNonKeyFor" + targetClass.replace(":", "0") + "\n");
            sb.append("\t a sh:NodeShape ;" + "\n");
            sb.append("\t sh:targetClass " + targetClass + " ;\n");
            RdfClass rdf = (RdfClass) par.getValue();

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
                if (rdf.getConditionalKeys().isEmpty()) {
                    sb.append("\t ) .\n");
                }
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
            if(rdf.getAlmostKeys().size() < 2 && rdf.getConditionalKeys().size() > 0){
                sb.replace(sb.length() -3, sb.length(), ";\n");
                sb.append("\t sh:or ( \n");
            }
            for (ConditionalKey conditionalKey : rdf.getConditionalKeys()){
                sb.append("\t\t [ \n");
                sb.append("\t\t\t sh:and ( \n");
                for (String att : conditionalKey.getKeyAttributes()){
                    if(!att.equals("a")) {
                        sb.append("\t\t\t\t [" + "\n");
                        sb.append("\t\t\t\t\t sh:path " + trimAttr(att) + " ;" + "\n");
                        sb.append("\t\t\t\t\t sh:minCount 1 ;" + "\n");
                        sb.append("\t\t\t\t ] \n");
                    }
                }
                Iterator it2 = conditionalKey.getConstraints().entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair = (Map.Entry)it2.next();
                        if(!pair.getKey().equals("a")) {
                            sb.append("\t\t\t\t [" + "\n");
                            sb.append("\t\t\t\t\t sh:path " + trimAttr(pair.getKey().toString()) + " ;" + "\n");
                            System.out.println(pair);
                            System.out.println("KEY: " + pair.getKey().toString() + ". VALUE: " + pair.getValue().toString());
                            if(pair.getValue().toString().contains("http://") || pair.getValue().toString().contains("https://")){
                                pair.setValue("\"" + pair.getValue() + "\"");
                            }
                            sb.append("\t\t\t\t\t sh:hasValue " + pair.getValue() + " ;\n");
                            sb.append("\t\t\t\t ] \n");
                        }
                }
                sb.append("\t\t\t ) \n");
                sb.append("\t\t ] \n");
                //sb.replace(sb.length() -3, sb.length(), ".\n\n");
            }
            if (!rdf.getConditionalKeys().isEmpty()) {
                sb.append("\t ) .\n");
            }

        }

        FileWriterWithEncoding fileWriter = null;
        try{
                String path = Configuration.getInstance().getShaclpath().replace(".ttl", "--MaxNonKeys.ttl");
                fileWriter = new FileWriterWithEncoding(new File(path), "UTF-8");
                fileWriter.write(sb.toString());
                fileWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String trimAttr(String att){
        if (att.equals("a")){
            return att;
        } else {
            try {
                return att.split("%&%")[1];
            } catch (Exception e) {
                System.out.println("The failed attribute is: " + att);
            }
        }
        return att.split("%&%")[1];
    }

    private String trimAttrPlus(String att){
        if (att.equals("a")){
            return att;
        } else {
            return att.split("%&%")[1].replace(":", "0");
        }
    }

    public void buildAlmostKeys(Model model, Map<String, RdfClass> classes) {
        StringBuilder sb = buildPrefixe(model);

        sb.append("schema:pre \n");
        sb.append("\t a owl:Ontology ; \n");
        sb.append("\t owl:imports sh: ; \n");
        for (Map.Entry<String, String> pair : model.getNsPrefixMap().entrySet()) {
            sb.append("\t\t\t sh:declare [ sh:prefix \"" + pair.getKey() + "\" ;\n \t\t\t\t\t\t sh:namespace \"" + pair.getValue() + "\"^^xsd:anyURI ;\n \t\t\t\t\t ]; \n");
        }
        sb.replace(sb.length() - 3, sb.length(), ". \n");
        sb.append("\n");
        Iterator it = classes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry par = (Map.Entry) it.next();

            String targetClass = par.getKey().toString();
            RdfClass rdf = (RdfClass) par.getValue();

            int counter = 0;
            for (Set<String> set : rdf.getAlmostKeys()) {

                sb.append("ex:AlmostKeyCheckFor" + targetClass.replace(":", "0") + counter + "\n");
                sb.append("\t a sh:NodeShape ;\n");
                sb.append("\t sh:target [\n");
                sb.append("\t\t sh:prefixes schema:pre ;\n");
                int n = 1;
                try {
                    n = Integer.parseInt(Controller.getInstance().tFAlmostKeys.getText());
                }catch (NoClassDefFoundError | ExceptionInInitializerError exc){

                }
                sb.append("\t\t sh:select \"\"\" \n");
                sb.append("\t\t\t SELECT $this0 \n");
                sb.append("\t\t\t WHERE { \n");
                String rndProperty = "";
                boolean rndPropertyIsSet = false;
                for (int i = 0; i <= n; i++) {
                    sb.append("\t\t\t\t $this" + i + " a " + targetClass + " .\n");
                    for (String att : set) {
                        if (!att.equals("a")) {
                            sb.append("\t\t\t\t $this" + i + " " + trimAttr(att) + " ?" + trimAttrPlus(att) + " .\n");
                            if (rndPropertyIsSet == false) {
                                rndProperty = trimAttr(att);
                            }
                        }
                    }
                }
                sb.append("\t\t\t\t FILTER (");
                for (int x = 0; x <= n; x++) {
                    for (int y = 0; y <= n; y++) {
                        if (x != y)
                            sb.append(" $this" + x + " != " + "$this" + y + " && ");
                    }
                }
                sb.replace(sb.length() - 3, sb.length(), ") \n");
                sb.append("\t\t\t } GROUP BY $this0 \n ");
                sb.append("\t\t\t \"\"\" ;\n");
                sb.append("\t ];\n");
                sb.append("\t sh:property [\n");
                sb.append("\t\t sh:path " + rndProperty + " ;\n");
                sb.append("\t\t sh:maxCount 0 ; \n");
                sb.append("\t ] . \n\n");

                counter = counter + 1;
            }

            FileWriterWithEncoding fileWriter = null;
            try{
                String path = Configuration.getInstance().getShaclpath().replace(".ttl", "--AlmostKeys.ttl");
                fileWriter = new FileWriterWithEncoding(new File(path), "UTF-8");
                StringWriter sw = new StringWriter();
                fileWriter.write(sb.toString());
                fileWriter.close();
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void buildAlmostASKKeys(Model model, Map<String, RdfClass> classes) {
        StringBuilder sb = new StringBuilder();

        Iterator it = classes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry par = (Map.Entry) it.next();

            String targetClass = par.getKey().toString();
            RdfClass rdf = (RdfClass) par.getValue();

            int counter = 0;
            for (Set<String> set : rdf.getAlmostKeys()) {

                sb.append("ex:AlmostKeyCheckFor" + targetClass.replace(":", "0") + counter + "\n");
                sb.append("\t a sh:SPARQLAskValidator ;\n");
                sb.append("\t sh:target [\n");
                sb.append("\t\t sh:prefixes [\n");
                for (Map.Entry<String, String> pair : model.getNsPrefixMap().entrySet()) {
                    sb.append("\t\t\t sh:declare [ sh:prefix \"" + pair.getKey() + "\" ;\n \t\t\t\t\t\t sh:namespace \"" + pair.getValue() + "\"^^xsd:anyURI ;\n \t\t\t\t\t ]; \n");
                }

                int n = 1;
                try {
                    n = Integer.parseInt(Controller.getInstance().tFAlmostKeys.getText());
                }catch (NoClassDefFoundError | ExceptionInInitializerError exc){

                }

                sb.append("\t\t ] ;\n");
                sb.append("\t\t sh:ask \"\"\" \n");
                sb.append("\t\t\t \n");
                sb.append("\t\t\t ASK WHERE { \n");
                String rndProperty = "";
                boolean rndPropertyIsSet = false;
                for (int i = 0; i <= n; i++) {
                    sb.append("\t\t\t\t $this" + i + " a " + targetClass + " .\n");
                    for (String att : set) {
                        if (!att.equals("a")) {
                            sb.append("\t\t\t\t $this" + i + " " + trimAttr(att) + " ?" + trimAttrPlus(att) + " .\n");
                            if (rndPropertyIsSet == false) {
                                rndProperty = trimAttr(att);
                            }
                        }
                    }
                }
                sb.append("\t\t\t\t FILTER (");
                for (int x = 0; x <= n; x++) {
                    for (int y = 0; y <= n; y++) {
                        if (x != y)
                            sb.append(" $this" + x + " != " + "$this" + y + " && ");
                    }
                }
                sb.replace(sb.length() - 3, sb.length(), ") \n");
                sb.append("\t\t\t } GROUP BY $this0 \n ");
                sb.append("\t\t\t \"\"\" ;\n");
                sb.append("\t ];\n");
                sb.append("\t sh:property [\n");
                sb.append("\t\t sh:path " + rndProperty + " ;\n");
                sb.append("\t\t sh:maxCount 0 ; \n");
                sb.append("\t ] . \n\n");

                counter = counter + 1;
            }


            FileWriterWithEncoding fileWriter = null;
            try{
                fileWriter = new FileWriterWithEncoding(new File(Configuration.getInstance().getShaclpath()), "UTF-8", true);
                StringWriter sw = new StringWriter();
                fileWriter.write(sb.toString());
                fileWriter.close();
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public int buildConditionalKeys(Model model, Map<String, RdfClass> classes) {
        StringBuilder prefixes = buildPrefixe(model);
        StringBuilder sb = prefixes;
        Iterator it = classes.entrySet().iterator();

        int shapefilecounter = 0;
        sb.append("schema:pre \n");
        sb.append("\t a owl:Ontology ; \n");
        sb.append("\t owl:imports sh: ; \n");
        for (Map.Entry<String, String> pair : model.getNsPrefixMap().entrySet()) {
            sb.append("\t\t\t sh:declare [ sh:prefix \"" + pair.getKey() + "\" ;\n \t\t\t\t\t\t sh:namespace \"" + pair.getValue() + "\"^^xsd:anyURI ;\n \t\t\t\t\t ]; \n");
        }
        sb.replace(sb.length() - 3, sb.length(), ". \n");
        sb.append("\n");
        while (it.hasNext()) {
            Map.Entry par = (Map.Entry) it.next();

            String targetClass = par.getKey().toString();
            RdfClass rdf = (RdfClass) par.getValue();

            int counter = 0;
            for (ConditionalKey conditionalKey : rdf.getConditionalKeys()) {

                sb.append("ex:ConditionalKeyCheckFor" + targetClass.replace(":", "0") + counter + "\n");
                sb.append("\t a sh:NodeShape ;\n");
                sb.append("\t sh:target [\n");
                sb.append("\t\t sh:prefixes schema:pre ;\n");
                int n = 1;
                try {
                    n = Integer.parseInt(Controller.getInstance().tFAlmostKeys.getText());
                }catch (NoClassDefFoundError | ExceptionInInitializerError exc){

                }
                sb.append("\t\t sh:select \"\"\" \n");
                sb.append("\t\t\t SELECT $this0 \n");
                sb.append("\t\t\t WHERE { \n");
                String rndProperty = "";
                boolean rndPropertyIsSet = false;
                for (int i = 0; i <= n; i++) {
                    sb.append("\t\t\t\t $this" + i + " a " + targetClass + " .\n");
                    for (String att : conditionalKey.getKeyAttributes()) {
                        if (!att.equals("a")) {
                            sb.append("\t\t\t\t $this" + i + " " + trimAttr(att) + " ?" + trimAttrPlus(att) + " .\n");
                            if (rndPropertyIsSet == false) {
                                rndProperty = trimAttr(att);
                            }
                        }
                    }
                    Iterator iter = conditionalKey.getConstraints().entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry pair = (Map.Entry)iter.next();
                        if (!pair.getKey().equals("a")) {
                            sb.append("\t\t\t\t $this" + i + " " + trimAttr(pair.getKey().toString()) + " " + pair.getValue() + " .\n");
                        }
                    }
                }
                sb.append("\t\t\t\t FILTER (");
                for (int x = 0; x <= n; x++) {
                    for (int y = 0; y <= n; y++) {
                        if (x != y)
                            sb.append(" $this" + x + " != " + "$this" + y + " && ");
                    }
                }
                sb.replace(sb.length() - 3, sb.length(), ") \n");
                sb.append("\t\t\t } GROUP BY $this0 \n ");
                sb.append("\t\t\t \"\"\" ;\n");
                sb.append("\t ];\n");
                sb.append("\t sh:property [\n");
                sb.append("\t\t sh:path " + rndProperty + " ;\n");
                sb.append("\t\t sh:maxCount 0 ; \n");
                sb.append("\t ] . \n\n");

                //counter++; ************************************************* comment to choose between approaches :^)

                //*************************************************************************************** A *****************************************************************
                try{
                    FileWriterWithEncoding fileWriter;
                    String path = Configuration.getInstance().getShaclpath().replace(".ttl", "--ConditionalKeys.ttl");
                    fileWriter = new FileWriterWithEncoding(new File(path), "UTF-8");
                    fileWriter.write(sb.toString());
                    fileWriter.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
                //**************************************************************************************** B *****************************************************************
                if(counter > 1000 ){
                    try{
                        FileWriterWithEncoding fileWriter;
                        String path = Configuration.getInstance().getShaclpath().replace(".ttl", "--ConditionalKeys - " + shapefilecounter + ".ttl");
                        fileWriter = new FileWriterWithEncoding(new File(path), "UTF-8");
                        fileWriter.write(sb.toString());
                        fileWriter.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    System.out.println("HashSet build: #" + (shapefilecounter));
                    shapefilecounter++;
                    sb.setLength(0);
                    sb = prefixes;
                    counter = 0;
                }
            }
        }
        return shapefilecounter;
    }


    @Override
    public void buildConditionalASKKeys(Model model, Map<String, RdfClass> classes) {
        StringBuilder sb = new StringBuilder();

        Iterator it = classes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry par = (Map.Entry) it.next();

            String targetClass = par.getKey().toString();
            RdfClass rdf = (RdfClass) par.getValue();

            int counter = 0;
            for (ConditionalKey conditionalKey : rdf.getConditionalKeys()) {

                sb.append("ex:ConditionalKeyCheckFor" + targetClass.replace(":", "0") + counter + "\n");
                sb.append("\t a sh:SPARQLAskValidator ;\n");
                /**
                sb.append("\t\t sh:prefixes [\n");
                for (Map.Entry<String, String> pair : model.getNsPrefixMap().entrySet()) {
                    sb.append("\t\t\t sh:declare [ sh:prefix \"" + pair.getKey() + "\" ;\n \t\t\t\t\t\t sh:namespace \"" + pair.getValue() + "\"^^xsd:anyURI ;\n \t\t\t\t\t ]; \n");
                }
                 sb.append("\t\t ] ;\n");
                 */

                int n = 1;
                try {
                    n = Integer.parseInt(Controller.getInstance().tFAlmostKeys.getText());
                }catch (NoClassDefFoundError | ExceptionInInitializerError exc){

                }
                sb.append("\t sh:message \"RAMBAZAMBA\" ; \n");
                sb.append("\t sh:ask \"\"\" \n");
                sb.append("\t\t ASK WHERE{ \n");
                String rndProperty = "";
                boolean rndPropertyIsSet = false;
                for (int i = 0; i <= n; i++) {
                    sb.append("\t\t\t $this" + i + " a " + targetClass + " .\n");
                    for (String att : conditionalKey.getKeyAttributes()) {
                        if (!att.equals("a")) {
                            sb.append("\t\t\t $this" + i + " " + trimAttr(att) + " ?" + trimAttrPlus(att) + " .\n");
                            if (rndPropertyIsSet == false) {
                                rndProperty = trimAttr(att);
                            }
                        }
                    }
                    Iterator iter = conditionalKey.getConstraints().entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry pair = (Map.Entry)iter.next();
                        if (!pair.getKey().equals("a")) {
                            sb.append("\t\t\t $this" + i + " " + trimAttr(pair.getKey().toString()) + " " + pair.getValue() + " .\n");
                        }
                    }
                }
                sb.append("\t\t\t FILTER (");
                for (int x = 0; x <= n; x++) {
                    for (int y = 0; y <= n; y++) {
                        if (x != y)
                            sb.append(" $this" + x + " != " + "$this" + y + " && ");
                    }
                }
                sb.replace(sb.length() - 3, sb.length(), ") \n");
                sb.append("\t\t } \n ");
                sb.append("\t\t \"\"\" .\n\n");
                counter = counter + 1;
            }


            FileWriterWithEncoding fileWriter = null;
            try{
                fileWriter = new FileWriterWithEncoding(new File(Configuration.getInstance().getShaclpath()), "UTF-8", true);
                StringWriter sw = new StringWriter();
                fileWriter.write(sb.toString());
                fileWriter.close();
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void build(HashMap<String, String> prefixes, Set<String> amk, String name, int almostKey) {

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

    @Override
    public void buildFunctionalDependencie(Model model, Map<String, RdfClass> classes) {
        StringBuilder sb = new StringBuilder();

        Iterator it = classes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry par = (Map.Entry) it.next();

            String targetClass = par.getKey().toString();
            RdfClass rdf = (RdfClass) par.getValue();

            int counter = 0;
            for (ConditionalKey conditionalKey : rdf.getConditionalKeys()) {

                sb.append("ex:FunctionalDependencieFor" + targetClass.replace(":", "0") + counter + "\n");
                sb.append("\t a sh:NodeShape ;\n");
                sb.append("\t sh:target [\n");
                sb.append("\t\t sh:prefixes schema:pre ;\n");
                int n = 1;
                try {
                    n = Integer.parseInt(Controller.getInstance().tFAlmostKeys.getText());
                }catch (NoClassDefFoundError | ExceptionInInitializerError exc){

                }
                sb.append("\t\t sh:select \"\"\" \n");
                sb.append("\t\t\t SELECT $this0 \n");
                sb.append("\t\t\t WHERE { \n");
                String rndProperty = "";
                boolean rndPropertyIsSet = false;
                for (int i = 0; i <= n; i++) {
                    sb.append("\t\t\t\t $this" + i + " a " + targetClass + " .\n");
                    for (String att : conditionalKey.getKeyAttributes()) {
                        if (!att.equals("a")) {
                            sb.append("\t\t\t\t $this" + i + " " + trimAttr(att) + " ?" + trimAttrPlus(att) + " .\n");
                            if (rndPropertyIsSet == false) {
                                rndProperty = trimAttr(att);
                            }
                        }
                    }
                    Iterator iter = conditionalKey.getConstraints().entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry pair = (Map.Entry)iter.next();
                        if (!pair.getKey().equals("a")) {
                            sb.append("\t\t\t\t $this" + i + " " + trimAttr(pair.getKey().toString()) + " " + pair.getValue() + " .\n");
                        }
                    }
                }
                sb.append("\t\t\t\t FILTER (");
                for (int x = 0; x <= n; x++) {
                    for (int y = 0; y <= n; y++) {
                        if (x != y)
                            sb.append(" $this" + x + " != " + "$this" + y + " && ");
                    }
                }
                sb.replace(sb.length() - 3, sb.length(), ") \n");
                sb.append("\t\t\t } GROUP BY $this0 \n ");
                sb.append("\t\t\t \"\"\" ;\n");
                sb.append("\t ];\n");
                sb.append("\t sh:property [\n");
                sb.append("\t\t sh:path " + rndProperty + " ;\n");
                sb.append("\t\t sh:maxCount 0 ; \n");
                sb.append("\t ] . \n\n");

                counter = counter + 1;
            }


            FileWriterWithEncoding fileWriter = null;
            try{
                fileWriter = new FileWriterWithEncoding(new File(Configuration.getInstance().getShaclpath()), "UTF-8", true);
                StringWriter sw = new StringWriter();
                fileWriter.write(sb.toString());
                fileWriter.close();
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }



}
