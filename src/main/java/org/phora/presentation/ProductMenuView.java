package org.phora.presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.phora.infrastructure.AppContext;

public class ProductMenuView {

    public static final double WIDTH  = 580;
    public static final double HEIGHT = 560;

    private final AppContext context;
    private final SceneManager sceneManager;

    public ProductMenuView(AppContext context, SceneManager sceneManager) {
        this.context = context;
        this.sceneManager = sceneManager;
    }

    public Scene createScene() {
        Label title = new Label("Productos");
        title.getStyleClass().add("menu-header");

        Label subtitle = new Label("Elegí qué operación querés realizar");
        subtitle.getStyleClass().add("menu-subheader");

        VBox heading = new VBox(4, title, subtitle);

        Hyperlink back = new Hyperlink("← Volver al panel");
        back.getStyleClass().add("logout-link");
        back.setOnAction(e -> sceneManager.showMainMenu());

        HBox top = new HBox(heading);
        HBox.setHgrow(heading, Priority.ALWAYS);
        top.getChildren().add(back);
        top.setAlignment(Pos.TOP_RIGHT);
        top.setPadding(new Insets(0, 0, 28, 0));

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(18);
        grid.setPadding(new Insets(4, 4, 4, 4));

        grid.add(buildCard("➕", "Dar de alta",  "Agregar un producto nuevo",
                () -> sceneManager.showProductForm(ProductFormView.Modo.ALTA)), 0, 0);
        grid.add(buildCard("✏️", "Modificar",    "Editar un producto existente",
                () -> sceneManager.showProductForm(ProductFormView.Modo.MODIFICAR)), 1, 0);
        grid.add(buildCard("🗑️", "Dar de baja",  "Eliminar un producto",
                () -> sceneManager.showProductForm(ProductFormView.Modo.BAJA)), 0, 1);
        grid.add(buildCard("🔍", "Buscar",        "Consultar por ID",
                () -> sceneManager.showProductForm(ProductFormView.Modo.BUSCAR)), 1, 1);
        grid.add(buildCard("📋", "Ver listado",   "Ver todos los productos",
                sceneManager::showProductList), 0, 2);

        // ScrollPane para que la 5ta tarjeta sea accesible sin agrandar la ventana
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: #1e2329; -fx-background-color: #1e2329;");

        VBox content = new VBox(top, scroll);
        content.setPadding(new Insets(40));
        VBox.setVgrow(scroll, Priority.ALWAYS);

        BorderPane root = new BorderPane(content);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    private VBox buildCard(String icon, String title, String description, Runnable action) {
        Label lblIcon  = new Label(icon);
        lblIcon.getStyleClass().add("feature-icon");

        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("feature-title");

        Label lblDesc  = new Label(description);
        lblDesc.getStyleClass().add("feature-desc");

        VBox card = new VBox(8, lblIcon, lblTitle, lblDesc);
        card.getStyleClass().add("feature-card");
        card.setPrefSize(220, 110);
        card.setOnMouseClicked(e -> action.run());
        return card;
    }
}