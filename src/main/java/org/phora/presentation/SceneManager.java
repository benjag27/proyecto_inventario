package org.phora.presentation;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.ScaleTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

        if (Math.abs(destinoW - stage.getWidth()) > 1 || Math.abs(destinoH - stage.getHeight()) > 1) {
            animateResizeTo(destinoW, destinoH);
        } else {
            lockSize(destinoW, destinoH);
            stage.centerOnScreen();
        }
    }

    private void animateResizeTo(double destinoW, double destinoH) {
        double inicioW = stage.getWidth();
        double inicioH = stage.getHeight();
        DoubleProperty ancho = new SimpleDoubleProperty(inicioW);
        DoubleProperty alto = new SimpleDoubleProperty(inicioH);
        ancho.addListener((obs, o, n) -> stage.setWidth(n.doubleValue()));
        alto.addListener((obs, o, n) -> stage.setHeight(n.doubleValue()));
        Timeline resize = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(ancho, inicioW, Interpolator.EASE_OUT),
                        new KeyValue(alto, inicioH, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(620),
                        new KeyValue(ancho, destinoW, Interpolator.EASE_OUT),
                        new KeyValue(alto, destinoH, Interpolator.EASE_OUT)));
        resize.setOnFinished(e -> {
            lockSize(destinoW, destinoH);
            stage.centerOnScreen();
        });
        resize.play();
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