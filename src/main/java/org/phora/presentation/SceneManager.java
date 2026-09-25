package org.phora.presentation;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    public void showChangePassword(String username) {
        ChangePasswordView changePasswordView =
                new ChangePasswordView(context.getLoginServiceUseCase(), this, username);
        freeSize();
        show(changePasswordView.createScene(), "Inventario — Cambiar contraseña");
        lockSize(ChangePasswordView.WIDTH, ChangePasswordView.HEIGHT);
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
        scene.getRoot().setOpacity(0);
        scene.setFill(Color.web("#1e2329"));
        stage.setScene(scene);
        stage.show();

        FadeTransition fade = new FadeTransition(Duration.millis(320), scene.getRoot());
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition zoom = new ScaleTransition(Duration.millis(320), scene.getRoot());
        zoom.setFromX(0.94);
        zoom.setFromY(0.94);
        zoom.setToX(1.0);
        zoom.setToY(1.0);
        zoom.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition abrir = new ParallelTransition(scene.getRoot(), fade, zoom);
        abrir.play();
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