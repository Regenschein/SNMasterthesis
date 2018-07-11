package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class ConstraintBuilderController {

    private static ConstraintBuilderController constraintBuilderController;
    public TableView tV_addConstraints;
    public TableColumn tC_Constraint;
    public TableColumn tC_Value;
    public ComboBox cmbBox_chooseConstraint;

    public ConstraintBuilderController(){
        this.constraintBuilderController = this;
    }


    public static synchronized ConstraintBuilderController getInstance() {
        if (ConstraintBuilderController.constraintBuilderController == null) {
            ConstraintBuilderController.constraintBuilderController = new ConstraintBuilderController();
        }
        return ConstraintBuilderController.constraintBuilderController;
    }

    void initialize(){

        tC_Constraint.setCellValueFactory(new PropertyValueFactory<ConstraintBuilderController.TableContent, SimpleStringProperty>("constraint"));
        tC_Value.setCellValueFactory(new PropertyValueFactory<ConstraintBuilderController.TableContent, SimpleStringProperty>("value"));


        tC_Constraint.setCellFactory(TextFieldTableCell.forTableColumn());
        tC_Value.setCellFactory(TextFieldTableCell.forTableColumn());

        tV_addConstraints.setEditable(true);
        tC_Constraint.setEditable(true);
        tC_Value.setEditable(true);
    }

    public class TableContent{
        private SimpleStringProperty constraint;
        private SimpleStringProperty value;
        public TableContent(SimpleStringProperty constraint, SimpleStringProperty value) {
            this.constraint = constraint;
            this.value = value;
        }
        public String getConstraint() {
            return constraint.get();
        }
        public SimpleStringProperty constraintProperty() {
            return constraint;
        }
        public void setConstraint(String constraint) {
            this.constraint = new SimpleStringProperty(constraint);
        }
        public String getValue() {
            return value.get();
        }
        public SimpleStringProperty valueProperty() {
            return value;
        }
        public void setValue(String value) {
            this.value = new SimpleStringProperty(value);
        }
    }

    public void addConstraint(ActionEvent actionEvent) {
        ConstraintBuilderController.TableContent tbc = new ConstraintBuilderController.TableContent(new SimpleStringProperty(
                cmbBox_chooseConstraint.getValue().toString()),
                new SimpleStringProperty(""));
        tV_addConstraints.getItems().add(tbc);
    }

    public void deleteRow(ActionEvent actionEvent) {
        ConstraintBuilderController.TableContent selectedItem = (ConstraintBuilderController.TableContent) tV_addConstraints.getSelectionModel().getSelectedItem();
        tV_addConstraints.getItems().remove(selectedItem);
    }

    public void changeConstraintCellEvent(TableColumn.CellEditEvent editEvent) {

    }

    public void changeValueCellEvent(TableColumn.CellEditEvent editEvent) {

    }

    public void addConstraints(ActionEvent actionEvent) {

    }

    public void chooseConstraint(ActionEvent actionEvent) {

    }
}
