package org.phora.presentation;

import javafx.scene.Scene;
import javafx.stage.Stage;

import org.phora.infrastructure.AppContext;

/**
 * Único punto de la app que conoce el Stage real. Cada vista solo
 * construye su propia Scene; SceneManager decide cuándo y cuál mostrar.
 */
public class SceneManager {

    private final Stage stage;
    private final AppContext context;

    public SceneManager(Stage stage, AppContext context) {
        this.stage = stage;
        this.context = context;
    }

    public void mostrarLogin() {
        LoginView loginView = new LoginView(context.getLoginServiceUseCase(), this);
        fijarTamano(LoginView.WIDTH, LoginView.HEIGHT);
        mostrar(loginView.crearScene(), "Inventario — Iniciar sesión");
    }

    public void mostrarMenuPrincipal() {
        MenuPrincipalView menuView = new MenuPrincipalView(context, this);

        mostrar(menuView.crearScene(), "Inventario — Panel principal");
    }

    public void mostrarInventario() {
        ProductView productView = new ProductView(context.getAddProductUseCase());

        mostrar(productView.crearScene(), "Inventario — Productos");
    }

    private void mostrar(Scene scene, String titulo) {
        stage.setTitle(titulo);
        stage.setScene(scene);
        stage.show();
    }

    /** Fija un tamaño exacto e impide maximizar/redimensionar la ventana. */
    private void fijarTamano(double width, double height) {
        stage.setResizable(false);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMinWidth(width);
        stage.setMaxWidth(width);
        stage.setMinHeight(height);
        stage.setMaxHeight(height);
    }

}