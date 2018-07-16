package modelbuilder;

import controller.Configuration;
import javatools.database.ResultIterator;
import modelbuilder.Modelreader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileManager;
import org.topbraid.jenax.util.JenaUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;

public class Modelreader {

    private Model model;
    private static Modelreader modelreader;

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
        model = ModelFactory.createDefaultModel();
        FileManager.get().readModel( model, Configuration.getInstance().getPath());

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




        // odel.listSubjectsWithProperty()
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
        FileWriter fileWriter = null;
        try{
            for(Map.Entry<String, Model> m : models.entrySet()){
                fileWriter = new FileWriter(Configuration.getClasspathes() + m.getKey().replace(":", "") + ".ttl");
                StringWriter sw = new StringWriter();
                RDFDataMgr.write(sw, m.getValue(), RDFFormat.TURTLE_BLOCKS) ;
                fileWriter.write(sw.toString());
                fileWriter.close();
            }

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
        StmtIterator si = Modelreader.getInstance().getModel().listStatements();
        while (si.hasNext()) {
            Statement statement = si.next();
            for (Map.Entry<String, RdfClass> rdfClass : ClassFinder.getInstance().getClasses().entrySet()) {
                String s = "";
                if (Modelreader.getInstance().getModel().getNsURIPrefix(statement.getSubject().getNameSpace()) == null) {
                    s = statement.getSubject().toString();
                } else
                    s = Modelreader.getInstance().getModel().getNsURIPrefix(statement.getSubject().getNameSpace()) + ":" + statement.getSubject().getLocalName();

                if (rdfClass.getValue().getInstances().contains(s)) {
                    Resource resource = Modelreader.getInstance().getModel().getResource(statement.getSubject().getURI());
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
        System.out.println("Hand hoch, dies tutet ein Brechpunkt sein.");
        writeClassFiles();
    }
}
