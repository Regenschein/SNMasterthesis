package modelbuilder;

import controller.Configuration;
import modelbuilder.Modelreader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

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
        transformPredicates(model);
    }

    private void transformPredicates(Model model){
        Property property = model.getProperty("a");
        System.out.println("test");
        //ResIterator resIterator = model.listSubjectsWithProperty(property);
        ResIterator resIterator = model.listSubjects();

        while(resIterator.hasNext()){
            Resource resource = resIterator.nextResource();
            System.out.println("I bims. Dem Ressource");
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
}
