package org.phora.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class ProductListView {

    public static final double WIDTH  = 700;
    public static final double HEIGHT = 520;

    private final AppContext context;
    private final SceneManager sceneManager;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final Label totalLabel = new Label();

    public ProductListView(AppContext context, SceneManager sceneManager) {
        this.context = context;
        this.sceneManager = sceneManager;
    }

    public Scene createScene() {
        Label title = new Label("Listado de productos");
        title.getStyleClass().add("menu-header");

        Label subtitle = new Label("Todos los productos registrados en el inventario");
        subtitle.getStyleClass().add("menu-subheader");

        VBox heading = new VBox(4, title, subtitle);

        Hyperlink back = new Hyperlink("← Volver");
        back.getStyleClass().add("logout-link");
        back.setOnAction(e -> sceneManager.showProductMenu());

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

        search("");

        VBox content = new VBox(12, top, searchField, table, totalLabel);
        content.setPadding(new Insets(32));

        BorderPane root = new BorderPane(content);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    private TableView<Product> buildTable() {
        TableColumn<Product, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, String> colName = new TableColumn<>("Nombre");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(260);

        TableColumn<Product, Double> colPrice = new TableColumn<>("Precio");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setPrefWidth(110);
        colPrice.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Product, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock.setPrefWidth(80);
        colStock.setStyle("-fx-alignment: CENTER;");

        TableView<Product> table = new TableView<>(productList);
        table.getColumns().addAll(colId, colName, colPrice, colStock);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No se encontraron productos."));
        table.setStyle("-fx-background-color: #2a3038; -fx-text-fill: #f4f6f8;");
        return table;
    }

    private void search(String query) {
        List<Product> results = context.getFindByNameUseCase().execute(query);
        productList.setAll(results);
        int count = results.size();
        totalLabel.setText(count + " producto" + (count == 1 ? "" : "s") + " encontrado" + (count == 1 ? "" : "s"));
    }
}