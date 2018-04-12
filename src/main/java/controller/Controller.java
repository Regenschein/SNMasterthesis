package controller;

import com.github.jsonldjava.core.JSONLDProcessingError;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import keyfinder.KeyfinderImplementation;
import keyfinder.RockerImplementation;
import model.Model;
import model.Triple;
import modelbuilder.*;
import querybuilder.Querybuilder;
import querybuilder.QuerybuilderImplementation;
import sakey.SAKey;
import shaclbuilder.Shaclbuilder;
import shaclbuilder.ShaclbuilderImplementation;
import keyfinder.VICKEY;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;

public class Controller {

    private static Controller controller;
    private static ResourceBundle bundle;

    public MenuItem file_new;
    public MenuItem edit_selectAll;
    public CheckBox checkBox_AlmostKey;
    public CheckBox checkBox_conditionalKey;
    public Button btn_generateRdf;
    public Button btn_loadModel;
    public Button btn_keys;
    public Button btn_shacl;
    public VBox vBox_left_side;
    public TextArea tA_main;
    public CheckBox checkBox_EntitiyKey;

    ClassFinder cf = new ClassFinder();

    public Controller(){
        this.controller = this;
        Locale locale = new Locale("en", "EN");
        bundle = ResourceBundle.getBundle("Properties/properties", locale);
    }

    public static synchronized Controller getInstance() {
        if (Controller.controller == null) {
            Controller.controller = new Controller();
        }
        return Controller.controller;
    }


    public void file_new(ActionEvent actionEvent) {
        System.out.println(bundle.getString("Controller.File.New"));
    }

    public void edit_selectAll(ActionEvent actionEvent) {

    }

    public void edit_unselectAll(ActionEvent actionEvent) {
        Querybuilder q = new QuerybuilderImplementation();
        q.build();
    }

    public void help_about(ActionEvent actionEvent) {

    }

    public void menu_cut(ActionEvent actionEvent) {

    }

    public void loadModel(ActionEvent actionEvent) {
        Transformer t = new TurtleToModelTransformator();
        System.out.println("Dest :D");
        t.transform("test");
        t = new ModelToTabSepTransformator();
        t.transform(Configuration.getInstance().getTsvpath());
        t = new ModelToN3Transformator();
        t.transform(Configuration.getInstance().getN3path());

        cf.build();
    }

    public void generateRdf(ActionEvent actionEvent) {
        System.out.println(bundle.getString("Controller.Edit.SelectAll"));
        Modelbuilder m = new ModelbuilderImplementation();
        m.build();
        System.out.println("T2");
    }

    public void almostKey(ActionEvent actionEvent) {
    }

    public void conditionalKey(ActionEvent actionEvent) {

    }

    public void keys(ActionEvent actionEvent) {
        if(checkBox_conditionalKey.isSelected()) {
            String[] args = new String[1];
            args[0] = Configuration.getInstance().getTsvpath();
            //args[0] = "./src/main/resources/data/museum.tsv";
            try {
                VICKEY.main(args);
                System.out.println(VICKEY.nonKeySet);
                cf.setNonKeys(VICKEY.nonKeySet);
                System.out.println("nks wr found");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }if(checkBox_AlmostKey.isSelected()){
            String[] args = new String[2];
            args[0] = Configuration.getInstance().getTsvpath();
            args[0] = "./src/main/resources/data/museum.tsv";
            System.out.println(args[0]);
            args[1] = "1";
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                System.out.println("Computing the non-keys");
                PrintStream ps = new PrintStream(baos);
                // IMPORTANT: Save the old System.out!
                PrintStream old = System.out;
                // Tell Java to use your special stream
                System.setOut(ps);
                // We do our stuff
                SAKey.main(args);
                // Put things back
                System.out.flush();
                // Restore everything
                System.setOut(old);
                // Show what happened
                String[] sakeyOutput = baos.toString().split("\n");
                for (String s : sakeyOutput)
                    System.out.println("SAKEY " + s);
            } catch (IOException  e) {
                e.printStackTrace();
            }
        }if(checkBox_EntitiyKey.isSelected()){
            String[] args = new String[5];
            args[0] = "-t";
            args[1] = "-i";
            //args[2] = Configuration.getInstance().getN3path();
            args[2] = Configuration.getInstance().getPath();
            //args[2] = Configuration.getInstance().getPath();
            args[3] = "-o";
            args[4] = "./src/main/resources/data/reuslt.nt";
            try{
                //KeyExtraction.main(args);
                KeyfinderImplementation.main(args);
                System.out.println("Done");
            } catch (IOException | JSONLDProcessingError | NoClassDefFoundError e) {
                //e.printStackTrace();
            }

            RockerImplementation r = new RockerImplementation();
            String[] args2 = new String[3];
            args2[0] = "Cheese-example";
            args2[1] = "./src/main/resources/data/cheeses-0.1.ttl";
            args2[2] = "./src/main/resources/ontologies/cheese.ttl";
            try {
                r.find(args2);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void shacl(ActionEvent actionEvent) {
        Shaclbuilder s = new ShaclbuilderImplementation();
        //s.build();

        Model model = Model.getInstance();
        s.buildNonKeys(model.getPrefixes(), cf.getClasses());
    }

    private void setDesign(){
        btn_generateRdf = new Button();
        btn_generateRdf.setMinWidth(vBox_left_side.getPrefWidth());
        btn_loadModel.setMinWidth(vBox_left_side.getPrefWidth());
        btn_keys.setMinWidth(vBox_left_side.getPrefWidth());
        btn_shacl.setMinWidth(vBox_left_side.getPrefWidth());
    }

    public void EntitiyKey(ActionEvent actionEvent) {


    }
}
