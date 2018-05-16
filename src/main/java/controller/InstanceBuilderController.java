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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;
import org.topbraid.jenax.util.JenaDatatypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        this.classname = classname;

        tC_newInstancePredicate.setCellValueFactory(new PropertyValueFactory<PredObj, String>("predicate"));
        tC_newInstanceObject.setCellValueFactory(new PropertyValueFactory<PredObj, SimpleStringProperty>("object"));
        tC_newInstanceObject.setCellFactory(TextFieldTableCell.forTableColumn());

        ClassFinder cf = ClassFinder.getInstance();
        RdfClass rdf = cf.getClasses().get(classname);

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
        //InstanceBuilderController.PredObj predObj = (InstanceBuilderController.PredObj) tV_newInstance.getSelectionModel().getSelectedItem();
        //predObj.setObject(editEvent.getNewValue().toString());
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
        ArrayList<String> als = getTableViewValues(tV_newInstance);
        System.out.println("Test");

        String uri =buildUri(tF_subjectName.getText());
        Resource subject = ResourceFactory.createResource(uri);
        for(int i = 0; i < als.size(); i = i +2) {
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
        }

        addClassInformation(subject);
    }

    private void addClassInformation(Resource subject){
        //Property predicate = Modelreader.getInstance().getModel().getProperty("a");
        //Property predicate = ResourceFactory.createProperty(buildUri("rdf:type"));
        Property predicate = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        //Property testicate = JenaDatatypes.getDatatypeURIs();
        Resource object = ResourceFactory.createResource(buildUri(this.classname));
        Modelreader.getInstance().getModel().add(subject, predicate, object);
    }

    private String buildUri(String s){
        Model made = Modelreader.getInstance().getModel();
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
