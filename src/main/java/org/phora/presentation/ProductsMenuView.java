package org.phora.presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.phora.infrastructure.AppContext;

/**
 * Submenú de la sección Productos: una tarjeta por operación
 * (alta, modificar, baja, buscar). Mismo lenguaje visual que
 * MenuPrincipalView, para mantener consistencia.
 */
public class ProductsMenuView {

    private final AppContext context;
    private final SceneManager sceneManager;
    public static final double WIDTH = 480;
    public static final double HEIGHT = 480;

    public ProductsMenuView(AppContext context, SceneManager sceneManager) {
        this.context = context;
        this.sceneManager = sceneManager;
    }

    public Scene createScene() {
        Label titulo = new Label("Productos");
        titulo.getStyleClass().add("menu-header");

        Label subtitulo = new Label("Elegí qué operación querés realizar");
        subtitulo.getStyleClass().add("menu-subheader");

        VBox encabezado = new VBox(4, titulo, subtitulo);

        Hyperlink volver = new Hyperlink("← Volver al panel");
        volver.getStyleClass().add("logout-link");
        volver.setOnAction(e -> sceneManager.showMainMenu());

        HBox top = new HBox(encabezado);
        HBox.setHgrow(encabezado, Priority.ALWAYS);
        top.getChildren().add(volver);
        top.setAlignment(Pos.TOP_RIGHT);
        top.setPadding(new Insets(0, 0, 28, 0));

        GridPane grilla = new GridPane();
        grilla.setHgap(18);
        grilla.setVgap(18);

        grilla.add(cretaTarget("➕", "Dar de alta", "Agregar un producto nuevo",
                () -> sceneManager.showProductoForm(ProductFormView.Modo.ALTA)), 0, 0);
        grilla.add(cretaTarget("✏️", "Modificar", "Editar un producto existente",
                () -> sceneManager.showProductoForm(ProductFormView.Modo.MODIFICAR)), 1, 0);
        grilla.add(cretaTarget("🗑️", "Dar de baja", "Eliminar un producto",
                () -> sceneManager.showProductoForm(ProductFormView.Modo.BAJA)), 0, 1);
        grilla.add(cretaTarget("🔍", "Buscar", "Consultar productos existentes",
                () -> sceneManager.showProductoForm(ProductFormView.Modo.BUSCAR)), 1, 1);

        VBox contenido = new VBox(top, grilla);
        contenido.setPadding(new Insets(40));

        BorderPane root = new BorderPane(contenido);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    private VBox cretaTarget(String icono, String titulo, String descripcion, Runnable accion) {
        Label lblIcono = new Label(icono);
        lblIcono.getStyleClass().add("feature-icon");

        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("feature-title");

        Label lblDesc = new Label(descripcion);
        lblDesc.getStyleClass().add("feature-desc");

        VBox tarjeta = new VBox(8, lblIcono, lblTitulo, lblDesc);
        tarjeta.getStyleClass().add("feature-card");
        tarjeta.setPrefSize(260, 130);
        tarjeta.setOnMouseClicked(e -> accion.run());

        return tarjeta;
    }
}