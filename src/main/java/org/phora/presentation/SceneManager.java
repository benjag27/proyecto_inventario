package org.phora.presentation;

import javafx.scene.Scene;
import javafx.stage.Stage;

import org.phora.domain.model.Product;
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

    public void showProductPanel() {
        ProductPanelView panelView = new ProductPanelView(context, this);
        freeSize();
        show(panelView.createScene(), "Inventario — Productos");
        lockSize(ProductPanelView.WIDTH, ProductPanelView.HEIGHT);
        stage.centerOnScreen();
    }

    public void showProductForm(ProductFormView.Modo modo) {
        showProductForm(modo, null);
    }

    public void showProductForm(ProductFormView.Modo modo, Product prefill) {
        ProductFormView formView = new ProductFormView(context, this, modo, prefill);
        freeSize();
        show(formView.createScene(), "Inventario — Productos");
        stage.sizeToScene();
        lockSize(stage.getWidth(), stage.getHeight());
        stage.centerOnScreen();
    }

    public void showAuditLogMenu() {
        AuditLogView auditLogView = new AuditLogView(context, this);
        freeSize();
        show(auditLogView.createScene(), "Inventario — Historial de Movimientos");
        lockSize(MainMenuView.WIDTH, MainMenuView.HEIGHT);
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