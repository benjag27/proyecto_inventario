package org.phora;

import javafx.application.Application;
import javafx.stage.Stage;

import org.phora.infrastructure.AppContext;
import org.phora.presentation.ProductView;
import org.phora.presentation.SceneManager;

public class Main extends Application {

    private static AppContext context;

    public static void main(String[] args) {
        // Inicializamos el núcleo de la aplicación antes de arrancar la interfaz
        context = new AppContext();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // SceneManager es el único que va a tocar primaryStage de ahora en más
        SceneManager sceneManager = new SceneManager(primaryStage);

        // ProductView solo construye su Scene, no conoce el Stage
        ProductView productView = new ProductView(context.getAddProductUseCase());

        sceneManager.mostrar(productView.crearScene(), "Gestión de Inventario");
    }
}