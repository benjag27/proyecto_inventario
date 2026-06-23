package org.phora;

import javafx.application.Application;
import javafx.stage.Stage;
import org.phora.infrastructure.AppContext;
import org.phora.presentation.ProductView;

public class Main extends Application {

    private static AppContext context;

    public static void main(String[] args) {
        // Inicializamos el núcleo de la aplicación antes de arrancar la interfaz
        context = new AppContext();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Instanciamos tu ProductView pasándole el caso de uso que necesita
        ProductView productView = new ProductView(context.getAddProductUseCase());

        // Aquí podrías usar tu SceneManager si maneja las ventanas, o iniciar la vista directo:
        productView.start(primaryStage);
    }
}