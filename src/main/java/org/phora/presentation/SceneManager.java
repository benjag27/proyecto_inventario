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
        showConTamano(loginView.createScene(), "Inventario — Iniciar sesión",
                LoginView.WIDTH, LoginView.HEIGHT);
    }

    public void showMainMenu() {
        MainMenuView menuView = new MainMenuView(context, this);
        showConTamano(menuView.createScene(), "Inventario — Panel principal",
                MainMenuView.WIDTH, MainMenuView.HEIGHT);
    }

    public void showProductPanel() {
        ProductPanelView panelView = new ProductPanelView(context, this);
        showConTamano(panelView.createScene(), "Inventario — Productos",
                ProductPanelView.WIDTH, ProductPanelView.HEIGHT);
    }

    public void showProductForm(ProductFormView.Modo modo) {
        showProductForm(modo, null);
    }

    public void showProductForm(ProductFormView.Modo modo, Product prefill) {
        ProductFormView formView = new ProductFormView(context, this, modo, prefill);
        Scene formScene = formView.createScene();
        showConTamano(formScene, "Inventario — Productos",
                formScene.getRoot().prefWidth(-1), formScene.getRoot().prefHeight(-1));
    }

    public void showAuditLogMenu() {
        AuditLogView auditLogView = new AuditLogView(context, this);
        showConTamano(auditLogView.createScene(), "Inventario — Historial de Movimientos",
                MainMenuView.WIDTH, MainMenuView.HEIGHT);
    }

    private void show(Scene scene, String titulo) {
        stage.setTitle(titulo);
        scene.getRoot().setOpacity(0);
        scene.setFill(Color.web("#1e2329"));
        stage.setScene(scene);
        stage.show();

        FadeTransition fade = new FadeTransition(Duration.millis(520), scene.getRoot());
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition zoom = new ScaleTransition(Duration.millis(320), scene.getRoot());
        zoom.setFromX(0.97);
        zoom.setFromY(0.97);
        zoom.setToX(1.0);
        zoom.setToY(1.0);
        zoom.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition abrir = new ParallelTransition(scene.getRoot(), fade, zoom);
        abrir.play();
    }

    public void showConTamano(Scene scene, String titulo, double destinoW, double destinoH) {
        freeSize();
        stage.setTitle(titulo);
        scene.getRoot().setOpacity(0);
        scene.setFill(Color.web("#1e2329"));
        stage.setScene(scene);
        stage.show();

        FadeTransition fade = new FadeTransition(Duration.millis(520), scene.getRoot());
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition zoom = new ScaleTransition(Duration.millis(520), scene.getRoot());
        zoom.setFromX(0.97);
        zoom.setFromY(0.97);
        zoom.setToX(1.0);
        zoom.setToY(1.0);
        zoom.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition abrir = new ParallelTransition(scene.getRoot(), fade, zoom);
        abrir.play();

        /* OTRA forma optimizada: sizeToScene() ajusta el Stage al tamaño EXACTO que el
           contenido necesita (JavaFX lo computa solo, nunca se corta) y recién ahí lockSize */
        stage.sizeToScene();
        lockSize(stage.getWidth(), stage.getHeight());
        stage.centerOnScreen();
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