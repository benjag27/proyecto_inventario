package org.phora.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.phora.domain.model.Product;
import org.phora.infrastructure.AppContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductFormView {

    public enum Modo { ALTA, MODIFICAR, BAJA, BUSCAR , BUSCAR_NOMBRE}

    private final AppContext context;
    private final SceneManager sceneManager;
    private final Modo modo;
    private final Product prefill;

    private final Label lblMessage = new Label();

    public ProductFormView(AppContext context, SceneManager sceneManager, Modo modo) {
        this(context, sceneManager, modo, null);
    }

    public ProductFormView(AppContext context, SceneManager sceneManager, Modo modo, Product prefill) {
        this.context = context;
        this.sceneManager = sceneManager;
        this.modo = modo;
        this.prefill = prefill;
    }

    public Scene createScene() {
        Label title = new Label(titleForMode());
        title.getStyleClass().add("login-title");

        Label subtitle = new Label(subtitleForMode());
        subtitle.getStyleClass().add("login-subtitle");

        Hyperlink back = new Hyperlink("← Volver");
        back.getStyleClass().add("logout-link");
        back.setOnAction(e -> sceneManager.showProductPanel());

        VBox heading = new VBox(4, title, subtitle);
        HBox top = new HBox(heading);
        HBox.setHgrow(heading, Priority.ALWAYS);
        top.getChildren().add(back);
        top.setAlignment(Pos.TOP_RIGHT);

        lblMessage.setVisible(false);
        lblMessage.setManaged(false);
        lblMessage.setWrapText(true);

        VBox fields = buildFields();

        VBox content = new VBox(20, top, fields, lblMessage);
        content.getStyleClass().add("login-card");
        content.setPadding(new Insets(40));
        content.setMaxWidth(480);
        content.setMinWidth(480);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setCenter(content);

        Scene scene = new Scene(root, 760, 540);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    private VBox buildFields() {
        switch (modo) {
            case ALTA:     return formAlta();
            case MODIFICAR: return formModificar();
            case BAJA:     return formBaja();
            case BUSCAR: return formBuscar();
            case  BUSCAR_NOMBRE: return formBuscarNombre();
            default: return formBuscar();
        }
    }

    // --- ALTA ---

    private VBox formAlta() {
        TextField txtName  = field("Nombre del producto");
        TextField txtPrice = field("Precio");
        TextField txtStock = field("Stock inicial");

        ObservableList<String> codes = FXCollections.observableArrayList();
        VBox barcodeBox = barcodeEditor(List.of(), codes);

        Button btn = primaryButton("Agregar producto");
        btn.setOnAction(e -> {
            try {
                String name  = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().replace(",", "."));
                int stock    = Integer.parseInt(txtStock.getText().trim());

                context.getAddProductUseCase().execute(name, price, stock, new ArrayList<>(codes), "admin");

                showMessage("Producto agregado correctamente.", false);
                txtName.clear();
                txtPrice.clear();
                txtStock.clear();
                codes.clear();
                barcodeBox.getChildren().clear();
                barcodeBox.getChildren().add(barcodeEditor(List.of(), codes));
            } catch (NumberFormatException ex) {
                showMessage("Precio y stock deben ser números válidos.", true);
            } catch (IllegalArgumentException ex) {
                showMessage(ex.getMessage(), true);
            }
        });

        return new VBox(12, txtName, txtPrice, txtStock, barcodeBox, btn);
    }

    // --- MODIFICAR ---

    private VBox formModificar() {
        TextField txtId    = field("ID del producto a modificar");
        TextField txtName  = field("Nuevo nombre");
        TextField txtPrice = field("Nuevo precio");
        TextField txtStock = field("Nuevo stock");

        ObservableList<String> codes = FXCollections.observableArrayList();
        List<String> initialCodes = prefill != null ? prefill.getBarcodes() : List.of();
        VBox barcodeBox = barcodeEditor(initialCodes, codes);

        if (prefill != null) {
            txtId.setText(String.valueOf(prefill.getId()));
            txtName.setText(prefill.getName());
            txtPrice.setText(String.valueOf(prefill.getPrice()));
            txtStock.setText(String.valueOf(prefill.getStock()));
        }

        Button btn = primaryButton("Guardar cambios");
        btn.setOnAction(e -> {
            try {
                int id       = Integer.parseInt(txtId.getText().trim());
                String name  = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().replace(",", "."));
                int stock    = Integer.parseInt(txtStock.getText().trim());

                boolean updated = context.getUpdateProductUseCase().execute(name, price, stock, new ArrayList<>(codes), id, "admin");

                if (updated) {
                    showMessage("Producto actualizado correctamente.", false);
                } else {
                    showMessage("No se encontró un producto con ese ID.", true);
                }
            } catch (NumberFormatException ex) {
                showMessage("ID, precio y stock deben ser números válidos.", true);
            } catch (IllegalArgumentException ex) {
                showMessage(ex.getMessage(), true);
            }
        });

        return new VBox(12, txtId, txtName, txtPrice, txtStock, barcodeBox, btn);
    }

    // --- BAJA ---

    private VBox formBaja() {
        TextField txtId = field("ID del producto a eliminar");
        if (prefill != null) txtId.setText(String.valueOf(prefill.getId()));

        Button btn = primaryButton("Eliminar producto");
        btn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                boolean deleted = context.getDeleteProductUseCase().execute(id,"admin");

                if (deleted) {
                    showMessage("Producto eliminado correctamente.", false);
                    txtId.clear();
                } else {
                    showMessage("No se encontró un producto con ese ID.", true);
                }
            } catch (NumberFormatException ex) {
                showMessage("El ID debe ser un número entero.", true);
            }
        });

        return new VBox(12, txtId, btn);
    }

    // --- BUSCAR ---

    private VBox formBuscar() {
        TextField txtId = field("ID del producto a buscar");
        if (prefill != null) txtId.setText(String.valueOf(prefill.getId()));

        Button btn = primaryButton("Buscar");
        btn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                Optional<Product> result = context.getFindProductUseCase().execute(id);

                if (result.isPresent()) {
                    Product p = result.get();
                    showMessage(
                            "Nombre: " + p.getName() +
                                    " | Precio: $" + String.format("%.2f", p.getPrice()) +
                                    " | Stock: " + p.getStock() +
                                    " | Códigos: " + barcodesLabel(p),
                            false
                    );
                } else {
                    showMessage("No se encontró un producto con ese ID.", true);
                }
            } catch (NumberFormatException ex) {
                showMessage("El ID debe ser un número entero.", true);
            }
        });

        return new VBox(12, txtId, btn);
    }
    private VBox formBuscarNombre() {
        TextField txtName = field("Nombre del producto a buscar");
        Button btn = primaryButton("Buscar por nombre");

        btn.setOnAction(e -> {
            String name = txtName.getText().trim();
            // Usamos el caso de uso que ya tenés inyectado en el AppContext
            var result = context.getFindByNameUseCase().execute(name);

            if (!result.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                result.forEach(p -> sb.append("ID: ").append(p.getId())
                        .append(" | ").append(p.getName())
                        .append(" | Stock: ").append(p.getStock())
                        .append(" | Códigos: ").append(barcodesLabel(p))
                        .append("\n"));
                showMessage(sb.toString().trim(), false);
            } else {
                showMessage("No se encontraron productos con ese nombre.", true);
            }
        });

        return new VBox(12, txtName, btn);
    }

    // --- Helpers ---

    /**
     * Editor de códigos de barras: campo + botón "Agregar" y lista de códigos
     * con un botón de quitar por fila. Permite agregar varios u eliminar uno
     * solo. Es opcional: la lista puede quedar vacía.
     */
    private VBox barcodeEditor(List<String> initial, ObservableList<String> codes) {
        codes.setAll(initial);

        Label lblCodes = new Label("Códigos de barras (opcional)");
        lblCodes.getStyleClass().add("menu-subheader");

        TextField txtCode = field("Código de barras");
        Button btnAdd = primaryButton("Agregar código");
        btnAdd.setPadding(new Insets(10, 14, 10, 14));

        HBox addRow = new HBox(8, txtCode, btnAdd);
        HBox.setHgrow(txtCode, Priority.ALWAYS);

        VBox list = new VBox(6);
        codes.addListener((ListChangeListener<? super String>) c -> renderBarcodes(codes, list));
        renderBarcodes(codes, list);

        btnAdd.setOnAction(e -> {
            String code = txtCode.getText().trim();
            if (code.isEmpty()) {
                showMessage("Ingresá un código de barras.", true);
                return;
            }
            if (codes.contains(code)) {
                showMessage("El código ya fue agregado a la lista.", true);
                return;
            }
            codes.add(code);
            txtCode.clear();
        });

        return new VBox(6, lblCodes, addRow, list);
    }

    private void renderBarcodes(ObservableList<String> codes, VBox list) {
        list.getChildren().clear();
        if (codes.isEmpty()) {
            Label empty = new Label("Sin códigos.");
            empty.getStyleClass().add("menu-subheader");
            list.getChildren().add(empty);
            return;
        }
        for (String code : codes) {
            Label lbl = new Label(code);
            lbl.getStyleClass().add("login-subtitle");
            Button btnRemove = new Button("✖");
            btnRemove.getStyleClass().add("btn-primary");
            btnRemove.setPrefWidth(40);
            HBox row = new HBox(10, lbl, btnRemove);
            HBox.setHgrow(lbl, Priority.ALWAYS);
            row.setAlignment(Pos.CENTER_LEFT);
            btnRemove.setOnAction(ev -> codes.remove(code));
            list.getChildren().add(row);
        }
    }

    private String barcodesLabel(Product p) {
        return p.getBarcodes().isEmpty() ? "-" : String.join(", ", p.getBarcodes());
    }

    private TextField field(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.getStyleClass().add("field");
        return f;
    }

    private Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-primary");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void showMessage(String text, boolean isError) {
        lblMessage.setText(text);
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);
        lblMessage.getStyleClass().removeAll("error-label", "success-label");
        lblMessage.getStyleClass().add(isError ? "error-label" : "success-label");
    }

    private String titleForMode() {
        switch (modo) {
            case ALTA:      return "Dar de alta";
            case MODIFICAR: return "Modificar producto";
            case BAJA:      return "Dar de baja";
            case BUSCAR:    return "Buscar producto";

            case BUSCAR_NOMBRE: return "Buscar por nombre";
            default:        return "Productos";
        }
    }

    private String subtitleForMode() {
        switch (modo) {
            case ALTA:      return "Completá los datos del nuevo producto";
            case MODIFICAR: return "Ingresá el ID y los nuevos valores";
            case BAJA:      return "Ingresá el ID del producto a eliminar";
            case BUSCAR:    return "Ingresá el ID del producto a consultar";

            case BUSCAR_NOMBRE: return "Ingresá el nombre o parte de él";
            default:        return "";
        }
    }
}