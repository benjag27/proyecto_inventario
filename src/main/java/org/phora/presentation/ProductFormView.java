package org.phora.presentation;

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

import java.util.Optional;

public class ProductFormView {

    public enum Modo { ALTA, MODIFICAR, BAJA, BUSCAR , BUSCAR_NOMBRE}

    private final AppContext context;
    private final SceneManager sceneManager;
    private final Modo modo;

    private final Label lblMessage = new Label();

    public ProductFormView(AppContext context, SceneManager sceneManager, Modo modo) {
        this.context = context;
        this.sceneManager = sceneManager;
        this.modo = modo;
    }

    public Scene createScene() {
        Label title = new Label(titleForMode());
        title.getStyleClass().add("login-title");

        Label subtitle = new Label(subtitleForMode());
        subtitle.getStyleClass().add("login-subtitle");

        Hyperlink back = new Hyperlink("← Volver");
        back.getStyleClass().add("logout-link");
        back.setOnAction(e -> sceneManager.showProductMenu());

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
        content.setMaxWidth(420);
        content.setMinWidth(420);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setCenter(content);

        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    private VBox buildFields() {
        switch (modo) {
            case ALTA:     return formAlta();
            case MODIFICAR: return formModificar();
            case BAJA:     return formBaja();
            case BUSCAR:
            default:       return formBuscar();
            case BUSCAR_NOMBRE: return formBuscarNombre();
        }
    }

    // --- ALTA ---

    private VBox formAlta() {
        TextField txtName  = field("Nombre del producto");
        TextField txtPrice = field("Precio");
        TextField txtStock = field("Stock inicial");

        Button btn = primaryButton("Agregar producto");
        btn.setOnAction(e -> {
            try {
                String name  = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().replace(",", "."));
                int stock    = Integer.parseInt(txtStock.getText().trim());

                context.getAddProductUseCase().execute(name, price, stock);

                showMessage("Producto agregado correctamente.", false);
                txtName.clear();
                txtPrice.clear();
                txtStock.clear();
            } catch (NumberFormatException ex) {
                showMessage("Precio y stock deben ser números válidos.", true);
            } catch (IllegalArgumentException ex) {
                showMessage(ex.getMessage(), true);
            }
        });

        return new VBox(12, txtName, txtPrice, txtStock, btn);
    }

    // --- MODIFICAR ---

    private VBox formModificar() {
        TextField txtId    = field("ID del producto a modificar");
        TextField txtName  = field("Nuevo nombre");
        TextField txtPrice = field("Nuevo precio");
        TextField txtStock = field("Nuevo stock");

        Button btn = primaryButton("Guardar cambios");
        btn.setOnAction(e -> {
            try {
                int id       = Integer.parseInt(txtId.getText().trim());
                String name  = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().replace(",", "."));
                int stock    = Integer.parseInt(txtStock.getText().trim());

                boolean updated = context.getUpdateProductUseCase().execute(name, price, stock, id);

                if (updated) {
                    showMessage("Producto actualizado correctamente.", false);
                } else {
                    showMessage("No se encontró un producto con ese ID.", true);
                }
            } catch (NumberFormatException ex) {
                showMessage("ID, precio y stock deben ser números válidos.", true);
            }
        });

        return new VBox(12, txtId, txtName, txtPrice, txtStock, btn);
    }

    // --- BAJA ---

    private VBox formBaja() {
        TextField txtId = field("ID del producto a eliminar");

        Button btn = primaryButton("Eliminar producto");
        btn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                boolean deleted = context.getDeleteProductUseCase().execute(id);

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
                                    " | Stock: " + p.getStock(),
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
                        .append("\n"));
                showMessage(sb.toString().trim(), false);
            } else {
                showMessage("No se encontraron productos con ese nombre.", true);
            }
        });

        return new VBox(12, txtName, btn);
    }

    // --- Helpers ---

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
            default:        return "Productos";
            case BUSCAR_NOMBRE: return "Buscar por nombre";
        }
    }

    private String subtitleForMode() {
        switch (modo) {
            case ALTA:      return "Completá los datos del nuevo producto";
            case MODIFICAR: return "Ingresá el ID y los nuevos valores";
            case BAJA:      return "Ingresá el ID del producto a eliminar";
            case BUSCAR:    return "Ingresá el ID del producto a consultar";
            default:        return "";
            case BUSCAR_NOMBRE: return "Ingresá el nombre o parte de él";
        }
    }
}