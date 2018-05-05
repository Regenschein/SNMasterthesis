package modelbuilder;

import controller.Configuration;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class Modelreader {

    private Model model;

    public void readFile(){
        model = ModelFactory.createDefaultModel();
        FileManager.get().readModel( model, Configuration.getInstance().getPath());
    }

    public void writeFile(Lang lang){

        if(lang.equals(Lang.TSV)){
            transformToTSV();

        } else{
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
                RDFDataMgr.write(sw, model, lang) ;
                fw.write(sw.toString());
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RDFDataMgr.write(System.out, model, lang) ;
        }
    }


    private void transformToTSV(){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(Configuration.getInstance().getTsvpath()));
            StmtIterator i = model.listStatements();
            while (i.hasNext()) {
                Statement curObj = i.next();
                String subj = Util.transformSubject(model, curObj);
                String pred = Util.transformPredicate(model, curObj);
                String obje = Util.transformObject(model, curObj);
                writer.write(subj + "\t" + pred + "\t" + obje + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Model getModel() {
        return model;
    }
}
