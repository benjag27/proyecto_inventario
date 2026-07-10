package org.phora;

import org.phora.infrastructure.AppContext;
import org.phora.presentation.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // El contexto vive durante toda la ejecución de la app
    private AppContext context;

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {



        SceneManager sceneManager = new SceneManager(primaryStage, context);

        // 3. Arrancamos el flujo
        sceneManager.showLogin();

    }
}