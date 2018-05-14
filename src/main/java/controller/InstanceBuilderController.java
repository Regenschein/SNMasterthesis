package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import modelbuilder.ClassFinder;
import modelbuilder.Modelreader;
import modelbuilder.RdfClass;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class InstanceBuilderController {

    private static InstanceBuilderController iBController;
    public TableView tV_newInstance;
    public TableColumn tC_newInstancePredicate;
    public TableColumn tC_newInstanceObject;
    public AnchorPane aP_main;
    public TextField tF_subjectName;

    public InstanceBuilderController(){
        this.iBController = this;
        //loadKey();
    }

    public static synchronized InstanceBuilderController getInstance() {
        if (InstanceBuilderController.iBController == null) {
            InstanceBuilderController.iBController = new InstanceBuilderController();
        }
        return InstanceBuilderController.iBController;
    }

    void loadKey(String classname){

        tC_newInstancePredicate.setCellValueFactory(new PropertyValueFactory<PredObj, String>("predicate"));
        tC_newInstanceObject.setCellValueFactory(new PropertyValueFactory<PredObj, String>("object"));
        tC_newInstanceObject.setCellFactory(TextFieldTableCell.forTableColumn());

        ClassFinder cf = ClassFinder.getInstance();
        RdfClass rdf = cf.getClasses().get(classname);

        ObservableList<String> l = FXCollections.observableArrayList();
        Set<String> removedDuplicates = new HashSet<String>();

        for (Set<String> hs :rdf.getAlmostKeys()){
            for (String s: hs){
                removedDuplicates.add(s.split("%§%")[1]);
            }
        }

        for (String s : removedDuplicates){
            PredObj preo = new PredObj(s, null);
            tV_newInstance.getItems().add(preo);
        }
        tV_newInstance.setEditable(true);
        tC_newInstanceObject.setEditable(true);
        System.out.println("bP");
    }

    public void changeCellEvent(TableColumn.CellEditEvent editEvent){
        PredObj predObj = (PredObj) tV_newInstance.getSelectionModel().getSelectedItem();
        predObj.setObject(editEvent.getNewValue().toString());

    }

    public class PredObj{

        private String predicate;
        private SimpleStringProperty object;

        public PredObj(String predicate, SimpleStringProperty object){
            this.predicate = predicate;
            this.object = object;
        }

        public String getPredicate() {
            return predicate;
        }

        public void setPredicate(String predicate) {
            this.predicate = predicate;
        }

        public SimpleStringProperty getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = new SimpleStringProperty(object);
        }
    }

    public void addNode(ActionEvent actionEvent) {
        ArrayList<String> als = getTableViewValues(tV_newInstance);
        System.out.println("Test");

        for(int i = 0; i < als.size(); i = i +2) {
            String uri =buildUri(tF_subjectName.getText());
            Resource subject = ResourceFactory.createResource(uri);
            Property predicate = Modelreader.getInstance().getModel().getProperty(buildUri(als.get(i)));
            Resource object = ResourceFactory.createResource(als.get(i+1));
            Modelreader.getInstance().getModel().add(subject, predicate, object);
        }

    }

    private String buildUri(String s){
        return Modelreader.getInstance().getModel().getNsPrefixURI(s.split(":")[0]) + s.split(":")[1];
    }

    private static ArrayList<String> getTableViewValues(TableView tableView) {
        ArrayList<String> values = new ArrayList<>();
        ObservableList<TableColumn> columns = tableView.getColumns();

        for (Object row : tableView.getItems()) {
            for (TableColumn column : columns) {
                values.add(
                        (String) column.
                                getCellObservableValue(row).
                                getValue().toString());
            }
        }

        return values;
    }
}
