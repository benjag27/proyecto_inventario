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

    public void showLogin() {
        LoginView loginView = new LoginView(context.getLoginServiceUseCase(), this);
        freeSize();
        show(loginView.createScene(), "Inventario — Iniciar sesión");
        lockSize(LoginView.WIDTH, LoginView.HEIGHT);
        stage.centerOnScreen();
    }

    public void showMainMenu() {
        MenuPrincipalView menuView = new MenuPrincipalView(context, this);
        freeSize();
        show(menuView.createScene(), "Inventario — Panel principal");
        lockSize(MenuPrincipalView.WIDTH, MenuPrincipalView.HEIGHT);
        stage.centerOnScreen();
    }

    /** Submenú de Productos: alta, modificar, baja, buscar. */
    public void showProductMenu() {
        ProductosMenuView productosMenuView = new ProductosMenuView(context, this);

        show(productosMenuView.createScene(), "Inventario — Productos");
        lockSize(ProductosMenuView.WIDTH, ProductosMenuView.HEIGHT);
        stage.centerOnScreen();
    }

    /** Formulario único, reutilizado para cada operación de producto. */
    public void showProductoForm(ProductoFormView.Modo modo) {
        ProductoFormView formView = new ProductoFormView(context, this, modo);
        show(formView.createScene(), "Inventario — Productos");
        stage.sizeToScene();
        lockSize(stage.getWidth(), stage.getHeight());
        stage.centerOnScreen();
    }

    private void show(Scene scene, String titulo) {
        stage.setTitle(titulo);
        stage.setScene(scene);
        stage.show();
    }

    /** Fija un tamaño exacto e impide maximizar/redimensionar la ventana. */
    private void lockSize(double width, double height) {

        if (stage.isMaximized()) {
            stage.setMaximized(false);
        }

        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMinWidth(width);
        stage.setMaxWidth(width);
        stage.setMinHeight(height);
        stage.setMaxHeight(height);
        stage.setResizable(false);
    }

    /** Libera las restricciones de tamaño para pantallas que sí deben poder redimensionarse. */
    private void freeSize() {

        if (stage.isMaximized()) {
            stage.setMaximized(false);
        }
        stage.setResizable(true);
        stage.setMinWidth(0);
        stage.setMinHeight(0);
        stage.setMaxWidth(Double.MAX_VALUE);
        stage.setMaxHeight(Double.MAX_VALUE);
    }
}