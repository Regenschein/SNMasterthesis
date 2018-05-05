package controller;

import com.github.jsonldjava.core.JSONLDProcessingError;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import keyfinder.RockerImplementation;
import model.Model;
import model.Triple;
import modelbuilder.*;
import org.apache.jena.riot.Lang;
import querybuilder.Querybuilder;
import querybuilder.QuerybuilderImplementation;
//import sakey.SAKey;
import keyfinder.SAKey;
import keyfinder.SAKeyAlmostKeysOneFileOneN;
import shaclbuilder.Shaclbuilder;
import shaclbuilder.ShaclbuilderImplementation;
import keyfinder.VICKEY;

import java.io.*;
import java.util.*;

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
    public TextArea tA_main = new TextArea();
    public CheckBox checkBox_EntitiyKey;
    public TextField tFAlmostKeys;
    public Button btn_chooseInput;
    public Button btn_transform;
    public ComboBox cmbBox_ChooseTarget;

    private String chosenFileType = "ttl";

    ClassFinder cf = new ClassFinder();
    Shaclbuilder s = new ShaclbuilderImplementation();
    Querybuilder q = new QuerybuilderImplementation();
    Modelreader mr = new Modelreader();

    public Controller(){
        this.controller = this;
        Locale locale = new Locale("en", "EN");
        bundle = ResourceBundle.getBundle("Properties/properties", locale);

        tA_main.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                tA_main.setScrollTop(Double.MAX_VALUE);
            }
        });
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
        Modelreader m = new Modelreader();
        m.readFile();
    }

    public void edit_unselectAll(ActionEvent actionEvent) {
        q.build();
    }

    public void help_about(ActionEvent actionEvent) {

    }

    public void menu_cut(ActionEvent actionEvent) {

    }

    public void loadModel(ActionEvent actionEvent) {
        mr.readFile();
        cf.build(mr.getModel());
    }

    public void generateRdf(ActionEvent actionEvent) {
        System.out.println(bundle.getString("Controller.Edit.SelectAll"));
        System.out.println("Lol. Hier passiert gar nichts mehr.");
        //Modelbuilder m = new ModelbuilderImplementation();
        //m.build();
        //System.out.println("T2");
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
                //cf.setNonKeys(VICKEY.nonKeySet);
                System.out.println("nks wr found");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }if(checkBox_AlmostKey.isSelected()){
            String[] args = new String[2];
            args[0] = Configuration.getInstance().getTsvpath();
            System.out.println(args[0]);
            if (tFAlmostKeys.getText().equals("")){
                args[1] = "0";
            } else {
                args[1] = tFAlmostKeys.getText();
            }
            System.out.println("argssss" + args[1]);
            try {
                SAKeyAlmostKeysOneFileOneN.main(args);
                cf.setAlmostKeys(mr.getModel(), SAKeyAlmostKeysOneFileOneN.almostKeys);
                System.out.println("Almost Keys were found");
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
            /**
            try{
                //KeyExtraction.main(args);
                KeyfinderImplementation.main(args);
                System.out.println("Done");
            } catch (IOException | JSONLDProcessingError | NoClassDefFoundError e) {
                //e.printStackTrace();
            }
             */

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
        s.buildNonKeys(mr.getModel(), cf.getClasses());
    }

    private void setDesign(){
        btn_generateRdf.setMinWidth(vBox_left_side.getPrefWidth());
        btn_loadModel.setMinWidth(vBox_left_side.getPrefWidth());
        btn_keys.setMinWidth(vBox_left_side.getPrefWidth());
        btn_shacl.setMinWidth(vBox_left_side.getPrefWidth());
    }

    public void EntitiyKey(ActionEvent actionEvent) {


    }

    public void evalShacl(ActionEvent actionEvent) {

        //s.build();
        HashMap<String, String> prefixes = Model.getInstance().getPrefixes();
        int almostKey = Integer.parseInt(tFAlmostKeys.getText());

        for (RdfClass rdfClass : cf.getClasses().values()){
            Set<Set<String>> almostKeys = rdfClass.getAlmostKeys();
            for (Set<String> amk: almostKeys){
                q.build(prefixes, amk, rdfClass.getName(), almostKey);
            }
        }

        s.build();
    }

    public void chooseInputFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File("./src/main/resources/data/"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Rdf-Formats", "*.ttl", "*.tsv", "*.rdf", "*.nt", "*.n3"),
                new FileChooser.ExtensionFilter("Rdf/XML", "*.rdf"),
                new FileChooser.ExtensionFilter("N-Triples", "*.nt*"),
                new FileChooser.ExtensionFilter("Notation-3", "*.n3*"),
                new FileChooser.ExtensionFilter("Tabular Seperated Values", "*.tsv"),
                new FileChooser.ExtensionFilter("Turtle Files", "*.ttl")
        );



        File selectedFile = fileChooser.showOpenDialog(Main.getGprimaryStage());
        if (selectedFile != null) {
            Main.getGprimaryStage().show();
            System.out.println(selectedFile.getPath().substring(selectedFile.getPath().length() -3));
            switch (selectedFile.getPath().substring(selectedFile.getPath().length() -3)){
                case "ttl" :
                    Configuration.getInstance().setPath(selectedFile.getPath());
                    //Configuration.getInstance().setTtlpath(selectedFile.getPath());
                    break;
                case ".n3" :
                    Configuration.getInstance().setPath(selectedFile.getPath());
                    //Configuration.getInstance().setTtlpath(selectedFile.getPath());
                    break;
                case "tsv" :
                    Configuration.getInstance().setPath(selectedFile.getPath());
                    //Configuration.getInstance().setTsvpath(selectedFile.getPath());
                    break;
                case ".nt" :
                    //Configuration.getInstance().setN3path(selectedFile.getPath());
                    Configuration.getInstance().setPath(selectedFile.getPath());
                    chosenFileType = ".nt";
                    break;
                case "rdf" :
                    break;
                default:
                    break;
            }

        }
    }

    public void transformModel(ActionEvent actionEvent) {
        Object o = cmbBox_ChooseTarget.getValue();
        System.out.println("test");
        switch (cmbBox_ChooseTarget.getValue().toString()){
            case "TSV":
                mr.writeFile(Lang.TSV);
                break;
            case "TTL":
                mr.writeFile(Lang.TTL);
                break;
            case "N3":
                mr.writeFile(Lang.N3);
                break;
            case "NT":
                mr.writeFile(Lang.NT);
                break;
            default :
                System.out.println("No transformator for the given Language.");
        }
    }

    public void chooseTransformTarget(ActionEvent actionEvent) {

    }
}
