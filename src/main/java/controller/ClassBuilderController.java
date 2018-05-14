package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Locale;
import java.util.ResourceBundle;

public class ClassBuilderController {

    private static ClassBuilderController classBuilder;

    public ClassBuilderController(){
        this.classBuilder = this;
    }

    public static synchronized ClassBuilderController getInstance() {
        if (classBuilder == null) {
            classBuilder = new ClassBuilderController();
        }
        return classBuilder;
    }
}
