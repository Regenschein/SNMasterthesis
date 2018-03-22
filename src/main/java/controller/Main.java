package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception{

        primaryStage.initStyle(StageStyle.DECORATED);

        Locale locale = new Locale("en", "EN");
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        ResourceBundle bundle = ResourceBundle.getBundle("Properties/properties", locale);

        Parent root = FXMLLoader.load(getClass().getResource("/gui.fxml"), bundle);

        primaryStage.setTitle("SNMasterthesis");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(700);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

}
