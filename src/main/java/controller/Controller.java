package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import modelbuilder.Modelbuilder;
import modelbuilder.ModelbuilderImplementation;
import querybuilder.Querybuilder;
import querybuilder.QuerybuilderImplementation;
import sakey.SAKey;
import shaclbuilder.Shaclbuilder;
import shaclbuilder.ShaclbuilderImplementation;
import vickey.VICKEY;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Controller {

    private static Controller controller;
    private static ResourceBundle bundle;

    public MenuItem file_new;
    public MenuItem edit_selectAll;
    public CheckBox checkBox_AlmostKey;
    public CheckBox checkBox_conditionalKey;

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
            //args[0] = "./src/main/resources/data/museum.tsv";
            args[0] = "./src/main/resources/data/world-0.1.tsv";
            try {
                VICKEY.main(args);
                System.out.println("Done");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }if(checkBox_AlmostKey.isSelected()){
            String[] args = new String[2];
            //args[0] = "./src/main/resources/data/museum.tsv";
            args[0] = "./src/main/resources/data/world-0.1.tsv";
            args[1] = "0";
            try {
                SAKey.main(args);
            } catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }

    public void shacl(ActionEvent actionEvent) {
        Shaclbuilder s = new ShaclbuilderImplementation();
        s.build();
    }
}
