package modelbuilder;

import model.Model;
import model.Triple;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ModelToN3Transformator implements Transformer{


    @Override
    public void transform(String file) {
        //RDFDataMgr.write(System.out, Model, Lang.TURTLE) ;



        BufferedWriter writer = null;
        Model model = Model.getInstance();
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (Triple triple : model.getTriples()){
                writer.write(triple.toN3());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
