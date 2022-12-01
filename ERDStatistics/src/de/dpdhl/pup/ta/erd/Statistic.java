/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author cyqjefe0019
 */
public class Statistic extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Statistic.fxml"));
        Parent root = fxmlLoader.load();
        Logger.getInstance().log("FXML loaded");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("de/dpdhl/pup/ta/erd/Stylesheet.css");
        stage.setScene(scene);
        stage.show();
        // Get Controller and initialize Data of view after construction of controls
        Controller controller = fxmlLoader.getController();
        controller.loadData();
        controller.setParentWindow(stage);
    }
    
    @Override
    public void init() {
        System.out.println("FXML Init");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
