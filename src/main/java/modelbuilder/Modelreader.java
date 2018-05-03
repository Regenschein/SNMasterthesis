package modelbuilder;

import controller.Configuration;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class Modelreader {

    Model m;

    public void readFile(){
        m = ModelFactory.createDefaultModel();
        FileManager.get().readModel( m, Configuration.getInstance().getPath());
    }

    public void writeFile(Model model, Lang lang){

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
        System.out.println("FEUER FREEEEEEEEEEEEI");
        RDFDataMgr.write(System.out, model, lang) ;
    }

    public void test(){
        writeFile(m, Lang.TURTLE);
        writeFile(m, Lang.NT);
        writeFile(m, Lang.N3);
    }

}
