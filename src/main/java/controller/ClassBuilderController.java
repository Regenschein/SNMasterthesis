package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import modelbuilder.ClassFinder;
import modelbuilder.RdfClass;

import java.awt.*;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public class ClassBuilderController {

    private static ClassBuilderController classBuilder;
    public TableView tV_newClass;
    public TableColumn tC_newClassName;
    public TableColumn tC_newClassDatatype;
    public TableColumn tC_newClassObligatory;
    public TableColumn tC_newClassConstraints;

    public ClassBuilderController(){
        this.classBuilder = this;
    }

    public static synchronized ClassBuilderController getInstance() {
        if (classBuilder == null) {
            classBuilder = new ClassBuilderController();
        }
        return classBuilder;
    }

    void initialize(){

        tC_newClassName.setCellValueFactory(new PropertyValueFactory<ClassBuilderController.TableContent, SimpleStringProperty>("name"));
        tC_newClassDatatype.setCellValueFactory(new PropertyValueFactory<ClassBuilderController.TableContent, SimpleStringProperty>("datatype"));
        tC_newClassObligatory.setCellValueFactory(new PropertyValueFactory<ClassBuilderController.TableContent, Boolean>("obligatory"));
        tC_newClassConstraints.setCellValueFactory(new PropertyValueFactory<ClassBuilderController.TableContent, Button>("constraints"));

        tC_newClassName.setCellFactory(TextFieldTableCell.forTableColumn());
        tC_newClassDatatype.setCellFactory(TextFieldTableCell.forTableColumn());

        tV_newClass.setEditable(true);
        tC_newClassDatatype.setEditable(true);
        tC_newClassName.setEditable(true);
        tC_newClassObligatory.setEditable(true);
        tC_newClassObligatory.setStyle( "-fx-alignment: CENTER;");
        tC_newClassConstraints.setEditable(true);
        tC_newClassConstraints.setStyle( "-fx-alignment: CENTER;");

    }

    public void changeNameCellEvent(TableColumn.CellEditEvent editEvent) {
        ClassBuilderController.TableContent tableContent = (ClassBuilderController.TableContent) tV_newClass.getSelectionModel().getSelectedItem();
        tableContent.setName(editEvent.getNewValue().toString());
    }

    public void changeDatatypeCellEvent(TableColumn.CellEditEvent editEvent) {
        ClassBuilderController.TableContent tableContent = (ClassBuilderController.TableContent) tV_newClass.getSelectionModel().getSelectedItem();
        tableContent.setDatatype(editEvent.getNewValue().toString());
    }

    public void changeObligatoryCellEvent(TableColumn.CellEditEvent editEvent) {

    }

    public void changeConstraintCellEvent(TableColumn.CellEditEvent editEvent) {
        //ClassBuilderController.TableContent tableContent = (ClassBuilderController.TableContent) tV_newClass.getSelectionModel().getSelectedItem();
        //tableContent.setConstraints(editEvent.getNewValue().toString());
    }

    public void addRow(ActionEvent actionEvent) {
        Button button = new Button("constraints");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("This is the way it isssssss");
            }
        });

        CheckBox checkBox = new CheckBox();
        checkBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Checkedi Check");
            }
        });

        ClassBuilderController.TableContent tbc = new ClassBuilderController.TableContent(new SimpleStringProperty(
                ""),
                new SimpleStringProperty(""),
                checkBox,
                button);
        tV_newClass.getItems().add(tbc);
    }

    public void createClass(ActionEvent actionEvent) {

    }

    public class TableContent{
        private SimpleStringProperty name;
        private SimpleStringProperty datatype;
        private CheckBox obligatory;
        private Button constraints;

        public TableContent(SimpleStringProperty name, SimpleStringProperty datatype, CheckBox obligatory, Button constraints) {
            this.name = name;
            this.datatype = datatype;
            this.obligatory = obligatory;
            this.constraints = constraints;
        }

        public String getName() {
            return name.get();
        }
        public SimpleStringProperty nameProperty() {
            return name;
        }
        public void setName(String name) {
            this.name = new SimpleStringProperty(name);
        }
        public String getDatatype() {
            return datatype.get();
        }
        public SimpleStringProperty datatypeProperty() {
            return datatype;
        }
        public void setDatatype(String datatype) {
            this.datatype = new SimpleStringProperty(datatype);
        }
        public CheckBox isObligatory() {
            return obligatory;
        }
        public void setObligatory(CheckBox obligatory) {
            this.obligatory = obligatory;
        }
        public Button getConstraints() {
            return constraints;
        }
        public void setConstraints(Button constraints) {
            this.constraints = constraints;
        }
    }
}
