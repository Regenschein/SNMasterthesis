package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import modelbuilder.ClassFinder;
import modelbuilder.Modelreader;
import modelbuilder.RdfClass;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;
import org.topbraid.jenax.util.JenaDatatypes;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.*;
import java.util.Optional;

public class InstanceBuilderController {

    private static InstanceBuilderController iBController;
    public TableView tV_newInstance;
    public TableColumn tC_newInstancePredicate;
    public TableColumn tC_newInstanceObject;
    public AnchorPane aP_main;
    public TextField tF_subjectName;

    private String classname;

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

        fill();

        this.classname = classname;

        tC_newInstancePredicate.setCellValueFactory(new PropertyValueFactory<ClassBuilderController.TableContent, SimpleStringProperty>("predicate"));
        tC_newInstanceObject.setCellValueFactory(new PropertyValueFactory<ClassBuilderController.TableContent, SimpleStringProperty>("object"));

        tC_newInstancePredicate.setCellFactory(TextFieldTableCell.forTableColumn());
        tC_newInstanceObject.setCellFactory(TextFieldTableCell.forTableColumn());

        tV_newInstance.setEditable(true);
        tC_newInstancePredicate.setEditable(true);
        tC_newInstanceObject.setEditable(true);

        ClassFinder cf = ClassFinder.getInstance();
        RdfClass rdf = cf.getClasses().get(classname);

        Set<String> removedDuplicates = new HashSet<String>();

        for (String attribute : rdf.getAttributes()){
            if (!attribute.equals("a")){
                tV_newInstance.getItems().add(createTBC(attribute.split("%&%")[1]));
            }
        }
    }

    public void changePredicateCellEvent(TableColumn.CellEditEvent editEvent) {
        InstanceBuilderController.TableContent tableContent = (InstanceBuilderController.TableContent) tV_newInstance.getSelectionModel().getSelectedItem();
        if(editEvent.getNewValue() != null)
            tableContent.setPredicate(editEvent.getNewValue().toString());
    }

    public void changeObjectCellEvent(TableColumn.CellEditEvent editEvent) {
        InstanceBuilderController.TableContent tableContent = (InstanceBuilderController.TableContent) tV_newInstance.getSelectionModel().getSelectedItem();
        if(editEvent.getNewValue() != null)
            tableContent.setObject(editEvent.getNewValue().toString());
    }

    public void addRow(ActionEvent actionEvent) {
        InstanceBuilderController.TableContent tbc = new InstanceBuilderController.TableContent(new SimpleStringProperty(
                ""),
                new SimpleStringProperty(""));
        tV_newInstance.getItems().add(tbc);
    }

    private InstanceBuilderController.TableContent createTBC(String pred){
        InstanceBuilderController.TableContent tbc = new InstanceBuilderController.TableContent(new SimpleStringProperty(pred),
                new SimpleStringProperty(""));
        return tbc;
    }

    public void fill(){
        Iterator iterator = model.Model.getInstance().getClasses().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            if(pair.getKey().equals(classname)) {
                System.out.println("teggn");
            }
        }
    }

    public void removeRow(ActionEvent actionEvent) {
        InstanceBuilderController.TableContent selectedItem = (InstanceBuilderController.TableContent) tV_newInstance.getSelectionModel().getSelectedItem();
        tV_newInstance.getItems().remove(selectedItem);
    }

    public class TableContent{
        private SimpleStringProperty predicate;
        private SimpleStringProperty object;

        public TableContent(SimpleStringProperty predicate, SimpleStringProperty object) {
            this.predicate = predicate;
            this.object = object;
        }

        public String getPredicate() {
            return predicate.get();
        }
        public SimpleStringProperty predicateProperty() {
            return predicate;
        }
        public void setPredicate(String predicate) {
            this.predicate = new SimpleStringProperty(predicate);
        }
        public String getObject() {
            return object.get();
        }
        public SimpleStringProperty objectProperty() {
            return object;
        }
        public void setObject(String object) {
            this.object = new SimpleStringProperty(object);
        }
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

        public String getObject() {
            try{
                return object.get();
            } catch (NullPointerException npe){
                return null;
            }

        }

        public void setObject(String object) {
            this.object = new SimpleStringProperty(object);
        }
    }

    public void addNode(ActionEvent actionEvent) {
        if(tF_subjectName.getText().isEmpty()){
            showAlert("An instance identifier is needed in the following format <namespace>:<identifier>");
        }
        else if(!tF_subjectName.getText().substring(1, tF_subjectName.getText().length() -2).contains(":")){
            showAlert("An instance identifier is needed in the following format <namespace>:<identifier>");
        } else {
            ArrayList<String> als = getTableViewValues(tV_newInstance);
            System.out.println("Test");
            String uri =buildUri(tF_subjectName.getText());
            Resource subject = ResourceFactory.createResource(uri);
            for(int i = 0; i < als.size(); i = i +2) {
                if(!als.get(i).isEmpty())
                if(als.get(i).substring(1, als.get(i).length() -2).contains(":")){
                    Property predicate = Modelreader.getInstance().getModel().getProperty(buildUri(als.get(i)));
                    Resource object;
                    String objectLit = als.get(i+1);
                    objectLit = objectLit.replace("StringProperty [value: ", "");
                    objectLit = objectLit.replace("]", "");
                    if(objectLit.contains("\"")){
                        //object = ResourceFactory.createResource(objectLit);
                        Literal literal;
                        if(objectLit.contains("@")){
                            String[] objectString = objectLit.split("@");
                            literal = ResourceFactory.createLangLiteral(objectString[0].replace("\"", ""), objectString[1]);
                        } else if(objectLit.contains("^^")){
                            String[] objectString = objectLit.split("\\^\\^");
                            RDFDatatype rtype = TypeMapper.getInstance().getSafeTypeByName(objectString[1]);
                            literal = ResourceFactory.createTypedLiteral(objectString[0].replace("\"", ""), rtype);
                        } else {
                            literal = ResourceFactory.createStringLiteral(objectLit);
                        }
                        Modelreader.getInstance().getModel().add(subject, predicate, literal);
                    } else {
                        if(objectLit.contains(":")){
                            object = ResourceFactory.createResource(buildUri(als.get(i+1)));
                            Modelreader.getInstance().getModel().add(subject, predicate, object);
                        } else {
                            try{
                                Literal literal;
                                if(objectLit.contains(".") || objectLit.contains(",")){
                                    float f = Float.parseFloat(objectLit.replace(",", "."));
                                    literal = ResourceFactory.createTypedLiteral(objectLit, TypeMapper.getInstance().getSafeTypeByName(buildUri("xsd:float")));
                                } else {
                                    int in = Integer.parseInt(objectLit);
                                    literal = ResourceFactory.createTypedLiteral(objectLit, TypeMapper.getInstance().getSafeTypeByName(buildUri("xsd:int")));
                                }
                                Modelreader.getInstance().getModel().add(subject, predicate, literal);
                            } catch (Exception e){

                            }
                        }
                    }
                    ClassFinder.getInstance().addInstanceToClass(classname, tF_subjectName.getText());
                } else {
                    showAlert("a namespace and an identifier is needed for every single predicate! <namespace>:<identifier>");
                }

            }

            addClassInformation(subject);
        }
    }

    private void addClassInformation(Resource subject){
        Property predicate = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource object = ResourceFactory.createResource(buildUri(this.classname));
        Modelreader.getInstance().getModel().add(subject, predicate, object);
    }

    private String buildUri(String s){
        Model made = Modelreader.getInstance().getModel();
        //TODO: Implement a check if we have a colon
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

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Wrong Format");

        alert.setHeaderText("Obacht!");
        alert.setContentText(message);

        alert.showAndWait();
    }
}
