package org.phora.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.phora.domain.model.Product;
import org.phora.infrastructure.AppContext;

import java.util.List;

/**
 * Pantalla principal del módulo Productos: muestra el listado del inventario
 * con buscador, y las operaciones (alta, modificar, baja, búsquedas) como
 * botones en un panel lateral. Modificar y dar de baja actúan sobre el
 * producto seleccionado en la tabla.
 */
public class ProductPanelView {

    public static final double WIDTH  = 1300;
    public static final double HEIGHT = 760;

    private final AppContext context;
    private final SceneManager sceneManager;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final Label totalLabel = new Label();
    private final Label feedbackLabel = new Label();

    public ProductPanelView(AppContext context, SceneManager sceneManager) {
        this.context = context;
        this.sceneManager = sceneManager;
    }

    public Scene createScene() {
        Label title = new Label("Productos");
        title.getStyleClass().add("menu-header");

        Label subtitle = new Label("Listado del inventario y operaciones");
        subtitle.getStyleClass().add("menu-subheader");

        VBox heading = new VBox(4, title, subtitle);

        Hyperlink back = new Hyperlink("← Volver al panel");
        back.getStyleClass().add("logout-link");
        back.setOnAction(e -> sceneManager.showMainMenu());

        HBox top = new HBox(heading);
        HBox.setHgrow(heading, Priority.ALWAYS);
        top.getChildren().add(back);
        top.setAlignment(Pos.TOP_RIGHT);
        top.setPadding(new Insets(0, 0, 16, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Buscar por nombre...");
        searchField.getStyleClass().add("field");
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> search(newVal));

        TableView<Product> table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        totalLabel.getStyleClass().add("menu-subheader");

        feedbackLabel.setWrapText(true);
        feedbackLabel.setVisible(false);
        feedbackLabel.setManaged(false);

        VBox listArea = new VBox(12, searchField, table, totalLabel);
        VBox.setVgrow(listArea, Priority.ALWAYS);

        VBox actions = buildSideActions(table);

        HBox center = new HBox(16, listArea, actions);
        HBox.setHgrow(listArea, Priority.ALWAYS);

        VBox content = new VBox(12, top, center);
        content.setPadding(new Insets(32));

        BorderPane root = new BorderPane(content);
        root.getStyleClass().add("root");

        search("");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    private TableView<Product> buildTable() {
        TableColumn<Product, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, String> colName = new TableColumn<>("Nombre");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(300);

        TableColumn<Product, Double> colPrice = new TableColumn<>("Precio");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setPrefWidth(130);
        colPrice.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Product, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock.setPrefWidth(100);
        colStock.setStyle("-fx-alignment: CENTER;");

        TableView<Product> table = new TableView<>(productList);
        table.getColumns().addAll(colId, colName, colPrice, colStock);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No se encontraron productos."));
        table.setStyle("-fx-background-color: #2a3038; -fx-text-fill: #f4f6f8;");
        return table;
    }

    private VBox buildSideActions(TableView<Product> table) {
        Label sideTitle = new Label("Operaciones");
        sideTitle.getStyleClass().add("feature-title");

        Button btnAlta = sideButton("➕ Dar de alta");
        btnAlta.setOnAction(e -> sceneManager.showProductForm(ProductFormView.Modo.ALTA));

        Button btnModificar = sideButton("✏️ Modificar");
        btnModificar.setOnAction(e -> {
            Product seleccionado = table.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                sceneManager.showProductForm(ProductFormView.Modo.MODIFICAR, seleccionado);
            } else {
                feedback("Seleccioná un producto de la lista para modificar.", true);
            }
        });

        Button btnBaja = sideButton("🗑️ Dar de baja");
        btnBaja.setOnAction(e -> {
            Product seleccionado = table.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                sceneManager.showProductForm(ProductFormView.Modo.BAJA, seleccionado);
            } else {
                sceneManager.showProductForm(ProductFormView.Modo.BAJA_BUSCAR);
            }
        });

        Button btnBuscarId = sideButton("🔍 Buscar por ID");
        btnBuscarId.setOnAction(e -> sceneManager.showProductForm(ProductFormView.Modo.BUSCAR));

        Button btnBuscarNombre = sideButton("🔎 Buscar por nombre");
        btnBuscarNombre.setOnAction(e -> sceneManager.showProductForm(ProductFormView.Modo.BUSCAR_NOMBRE));

        VBox box = new VBox(12, sideTitle, btnAlta, btnModificar, btnBaja, btnBuscarId, btnBuscarNombre, feedbackLabel);
        box.getStyleClass().add("feature-card");
        box.setPrefWidth(250);
        box.setMinWidth(250);
        box.setMaxWidth(250);
        return box;
    }

    private Button sideButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-primary");
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void search(String query) {
        List<Product> results = context.getFindByNameUseCase().execute(query);
        productList.setAll(results);
        int count = results.size();
        totalLabel.setText(count + " producto" + (count == 1 ? "" : "s") + " encontrado" + (count == 1 ? "" : "s"));
    }

    private void feedback(String text, boolean isError) {
        feedbackLabel.setText(text);
        feedbackLabel.setVisible(true);
        feedbackLabel.setManaged(true);
        feedbackLabel.getStyleClass().removeAll("error-label", "success-label");
        feedbackLabel.getStyleClass().add(isError ? "error-label" : "success-label");
    }
}