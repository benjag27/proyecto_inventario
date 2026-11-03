package org.phora;

import org.phora.infrastructure.AppContext;
import org.phora.presentation.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private AppContext context;

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.context = new org.phora.infrastructure.AppContext();


        SceneManager sceneManager = new SceneManager(primaryStage, context);

        sceneManager.showLogin();

    }
}