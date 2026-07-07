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

    public void showLogin() {
        LoginView loginView = new LoginView(context.getLoginServiceUseCase(), this);
        freeSize();
        show(loginView.createScene(), "Inventario — Iniciar sesión");
        lockSize(LoginView.WIDTH, LoginView.HEIGHT);
        stage.centerOnScreen();
    }

    public void showMainMenu() {
        MainMenuView menuView = new MainMenuView(context, this);
        freeSize();
        show(menuView.createScene(), "Inventario — Panel principal");
        lockSize(MainMenuView.WIDTH, MainMenuView.HEIGHT);
        stage.centerOnScreen();
    }

    public void showProductMenu() {
        ProductMenuView productMenuView = new ProductMenuView(context, this);
        freeSize();
        show(productMenuView.createScene(), "Inventario — Productos");
        lockSize(ProductMenuView.WIDTH, ProductMenuView.HEIGHT);
        stage.centerOnScreen();
    }

    public void showProductList() {
        ProductListView listView = new ProductListView(context, this);
        freeSize();
        show(listView.createScene(), "Inventario — Listado de productos");
        lockSize(ProductListView.WIDTH, ProductListView.HEIGHT);
        stage.centerOnScreen();
    }

    public void showProductForm(ProductFormView.Modo modo) {
        ProductFormView formView = new ProductFormView(context, this, modo);
        freeSize();
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

    private void lockSize(double width, double height) {
        if (stage.isMaximized()) stage.setMaximized(false);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMinWidth(width);
        stage.setMaxWidth(width);
        stage.setMinHeight(height);
        stage.setMaxHeight(height);
        stage.setResizable(false);
    }

    private void freeSize() {
        if (stage.isMaximized()) stage.setMaximized(false);
        stage.setResizable(true);
        stage.setMinWidth(0);
        stage.setMinHeight(0);
        stage.setMaxWidth(Double.MAX_VALUE);
        stage.setMaxHeight(Double.MAX_VALUE);
    }
}