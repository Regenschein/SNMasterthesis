package modelbuilder;

import model.Model;
import model.Triple;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ModelToTabSepTransformator implements Transformer{


    @Override
    public void transform(String file) {
        BufferedWriter writer = null;
        Model model = Model.getInstance();
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (Triple triple : model.getTriples()){
                writer.write(triple.toTSV());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
