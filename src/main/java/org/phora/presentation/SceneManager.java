package org.phora.presentation;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.phora.infrastructure.AppContext;

public class SceneManager {
    private final Stage stage;
    private final AppContext context;

    public SceneManager(Stage stage, AppContext context) {
        this.stage = stage;
        this.context = context;
    }

    public void mostrarLogin() {
        LoginView loginView = new LoginView(context.getLoginServiceUseCase(), this);
        stage.setScene(loginView.crearScene());
        stage.setTitle("Login");
        stage.show();
    }

    public void mostrarInventario() {
        ProductView productView = new ProductView(context.getAddProductUseCase());
        stage.setScene(productView.crearScene());
        stage.setTitle("Gestión de Inventario");
    }
}