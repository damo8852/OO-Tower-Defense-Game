package com.towerdefense.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TowerDefenseApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label placeholder = new Label("Tower Defense — Step 1 scaffold");
        StackPane root = new StackPane(placeholder);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Tower Defense");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
