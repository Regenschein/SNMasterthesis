package modelbuilder;

import controller.Configuration;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.jena.ext.com.google.common.base.Utf8;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileManager;
import org.topbraid.jenax.util.JenaUtil;

import javax.swing.plaf.nimbus.State;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Predicate;

public class Modelreader {

    private Model model;
    private static Modelreader modelreader;
    private static long modelSize;
    private static int subjectSize;
    private static int predSize;
    //private static HashSet<Property> propertyHashSet = new HashSet<>();
    private static HashSet<String> propertyHashSet = new HashSet<>();
    private static int itCounter = 0;

    public Modelreader(){
        this.modelreader = this;
        //loadKey();
    }

    public static synchronized Modelreader getInstance() {
        if (Modelreader.modelreader == null) {
            Modelreader.modelreader = new Modelreader();
        }
        return Modelreader.modelreader;
    }


    public void readFile(){
        this.model = ModelFactory.createDefaultModel();
        FileManager.get().readModel( this.model, Configuration.getInstance().getPath());
        generateInfos();
    }

    public void readFilePlus(){
        this.model = ModelFactory.createDefaultModel();
        FileManager.get().readModel( this.model, Configuration.getInstance().getPath());
        generateInfos();
        StmtIterator stmtIterator = this.model.listStatements();
        HashSet<Resource> resourceHashSet = new HashSet<Resource>();
        while (stmtIterator.hasNext()){
            Statement fact = stmtIterator.next();
            if(fact.getObject().toString().equals("http://wikiba.se/ontology-beta#Statement")){
                resourceHashSet.add(fact.getSubject());
            }
        }

        LinkedList<Statement> statements = new LinkedList<Statement>();
        stmtIterator = this.model.listStatements();
        while(stmtIterator.hasNext()){
            Statement fact = stmtIterator.next();
            if(resourceHashSet.contains(fact.getSubject())){
                statements.add(fact);
            }
        }

        this.model = this.model.remove(statements);
    }

    public void readFile(String path){
        model = ModelFactory.createDefaultModel();
        FileManager.get().readModel( model, path);
        generateInfos();
    }

    public void generateInfos(){
        System.out.println("Start the generating process!");
        long modelsize = model.size();
        System.out.println("Modelsize: " + modelsize);
        StmtIterator stmtIterator = model.listStatements();
        HashSet<Property> predicates = new HashSet<Property>();
        HashSet<Resource> subjectsWithATag = new HashSet<Resource>();
        HashSet<Resource> subjects = new HashSet<Resource>();
        while (stmtIterator.hasNext()){
            Statement s = stmtIterator.next();

            predicates.add(s.getPredicate());
            if (s.getPredicate().getURI().contains("rdf-syntax-ns#type")){
                subjectsWithATag.add(s.getSubject());
            }
            if(!s.getSubject().isAnon()){
                subjects.add(s.getSubject());
            }
        }
        System.out.println("Subjects: " + subjects.size());
        System.out.println("Predicates: " + predicates.size());

        System.out.println("Subjects with \"a\" tag: " + subjectsWithATag.size() + "; Subjects without: \"a\" tag: " + (subjects.size() - subjectsWithATag.size()));

        modelSize += modelsize;
        subjectSize += subjects.size();
        for (Property p : predicates){
            propertyHashSet.add(p.getURI());
        }
        itCounter++;
        System.out.println("Iteration #" + itCounter + ": Modelsize: " + modelSize + ". subjectSize: " + subjectSize + ". propertySize: " + propertyHashSet.size());
        try{
            FileWriter fileWriter = new FileWriter("statistics_" +Configuration.getPath().split("/")[Configuration.getPath().split("/").length - 1].split("\\.")[0], true);
            fileWriter.write("Iteration #" + itCounter + ": Modelsize: " + modelSize + ". subjectSize: " + subjectSize + ". propertySize: " + propertyHashSet.size() + "\n");
            fileWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeFile(Lang lang){
        FileWriter fw = null;
        try {
            String s = "";
            if (lang.equals(Lang.TTL)){
                s = Configuration.getTtlpath();
            } else if (lang.equals(Lang.N3)){
                s = Configuration.getN3path();
            } else if (lang.equals(Lang.NT)) {
                s = Configuration.getNtpath();
            } else if (lang.equals(Lang.RDFXML)) {
                s = Configuration.getRdfpath();
            }
            fw = new FileWriter(s);
            StringWriter sw = new StringWriter();
            RDFDataMgr.write(sw, model, lang) ;
            fw.write(sw.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RDFDataMgr.write(System.out, model, lang) ;
    }

    public void writeClassFiles(){
        //FileWriter fileWriter = null;
        FileWriterWithEncoding fileWriter = null;
        try{
            for(Map.Entry<String, Model> m : models.entrySet()){
                fileWriter = new FileWriterWithEncoding(new File(Configuration.getClasspathes() + m.getKey().replace(":", "") + ".ttl"), "UTF-8");
                StringWriter sw = new StringWriter();
                RDFDataMgr.write(sw, m.getValue(), RDFFormat.TURTLE_BLOCKS) ;
                fileWriter.write(sw.toString());
                fileWriter.close();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeClassFile(String classname, Model model){
        FileWriter fileWriter = null;
        try{
            File tmpDir = new File(Configuration.getClasspathes() + classname.replace(":", "") + ".ttl");
            boolean exists = tmpDir.exists();
            if(exists == true){
                model.clearNsPrefixMap();
            }
            fileWriter = new FileWriter(Configuration.getClasspathes() + classname.replace(":", "") + ".ttl", true);
            StringWriter sw = new StringWriter();
            RDFDataMgr.write(sw, model, RDFFormat.TURTLE_BLOCKS) ;
            fileWriter.write(sw.toString());
            fileWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeFile(Lang lang, ClassFinder classFinder){
        transformToTSV(classFinder);
    }


    private void transformToTSV(ClassFinder classFinder){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(Configuration.getInstance().getTsvpath()));
            StmtIterator i = model.listStatements();
            while (i.hasNext()) {
                Statement curObj = i.next();
                String subj = Util.transformSubject(model, curObj);
                String pred = Util.transformPredicate(model, curObj, returnClassName(subj, classFinder));
                String obje = Util.transformObject(model, curObj);
                writer.write(subj + "\t" + pred + "\t" + obje + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String returnClassName(String subject, ClassFinder classFinder){
        return classFinder.retrieveClass(subject);
    }

    public Model getModel() {
        return model;
    }

    HashMap<String, Model> models = new HashMap<String, Model>();

    public void mine() {

        for (String name : ClassFinder.getInstance().getClasses().keySet()) {
            models.put(name, JenaUtil.createDefaultModel().setNsPrefixes(model.getNsPrefixMap()));
        }
        //StmtIterator si = Modelreader.getInstance().getModel().listStatements();
        StmtIterator si = model.listStatements();
        while (si.hasNext()) {
            Statement statement = si.next();
            for (Map.Entry<String, RdfClass> rdfClass : ClassFinder.getInstance().getClasses().entrySet()) {
                String s = "";
                if (model.getNsURIPrefix(statement.getSubject().getNameSpace()) == null) {
                    s = statement.getSubject().toString();
                } else
                    s = model.getNsURIPrefix(statement.getSubject().getNameSpace()) + ":" + statement.getSubject().getLocalName();

                if (rdfClass.getValue().getInstances().contains(s)) {
                    Resource resource = model.getResource(statement.getSubject().getURI());
                    for (StmtIterator it = resource.listProperties(); it.hasNext(); ) {
                        Statement st = it.next();
                        models.get(rdfClass.getKey()).add(resource, st.getPredicate(), st.getObject());
                    }
                }
                /**
                if (statement.getPredicate().equals("a") || statement.getPredicate().equals("rdf-syntax-ns#type")) {
                    rdfClass.getValue().getInstances();
                }*/
            }
        }
        writeClassFiles();
    }

    public void farmClasses() {
        BufferedReader bufferedReader = null;
        String filePath = Configuration.getPath();
        File file = new File(filePath);
        HashSet<String> classnames = new HashSet<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while (null != (line = bufferedReader.readLine())) {
                if (line.contains(" a ") || line.contains("rdfs:type")){
                    String[] ary = line.split(" a ");
                    String classname = ary[1];
                    classnames.add(classname);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for(String s : classnames){
            System.out.println(s);
        }
    }
}
