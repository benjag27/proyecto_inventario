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
        // 1. Inicializamos el contexto UNA SOLA VEZ aquí

        this.context = new AppContext();

        SceneManager sceneManager = new SceneManager(primaryStage, context);

        // 3. Arrancamos el flujo
        sceneManager.showLogin();

    }
}