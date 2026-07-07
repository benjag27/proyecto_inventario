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

import org.phora.application.AddProduct;
import org.phora.application.DeleteProduct;
import org.phora.application.FindProduct;
import org.phora.application.UpdateProduct;
import org.phora.domain.model.Product;
import org.phora.infrastructure.AppContext;

import java.util.Optional;

/**
 * Un único formulario que se adapta según el Modo recibido:
 * ALTA, MODIFICAR, BAJA o BUSCAR. Cada modo muestra solo los
 * campos que necesita y llama al caso de uso correspondiente.
 */
public class ProductFormView {

    public enum Modo { ALTA, MODIFICAR, BAJA, BUSCAR }

    private final AppContext context;
    private final SceneManager sceneManager;
    private final Modo modo;

    private final Label lblMensaje = new Label();


    public ProductFormView(AppContext context, SceneManager sceneManager, Modo modo) {
        this.context = context;
        this.sceneManager = sceneManager;
        this.modo = modo;
    }

    public Scene createScene() {
        Label titulo = new Label(tituloSegunModo());
        titulo.getStyleClass().add("login-title");

        Label subtitulo = new Label(subtituloSegunModo());
        subtitulo.getStyleClass().add("login-subtitle");

        Hyperlink volver = new Hyperlink("← Volver");
        volver.getStyleClass().add("logout-link");
        volver.setOnAction(e -> sceneManager.showProductMenu());

        VBox encabezado = new VBox(4, titulo, subtitulo);
        HBox top = new HBox(encabezado);
        HBox.setHgrow(encabezado, Priority.ALWAYS);
        top.getChildren().add(volver);
        top.setAlignment(Pos.TOP_RIGHT);

        VBox campos = buildField();

        lblMensaje.getStyleClass().add("error-label");
        lblMensaje.setVisible(false);
        lblMensaje.setManaged(false);
        lblMensaje.setWrapText(true);

        VBox contenido = new VBox(20, top, campos, lblMensaje);
        contenido.getStyleClass().add("login-card");
        contenido.setPadding(new Insets(40));
        contenido.setMaxWidth(400);
        contenido.setMinWidth(400);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setCenter(contenido);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    // ---------- Construcción de campos según el modo ----------

    private VBox buildField() {
        switch (modo) {
            case ALTA:
                return formDischarge();
            case MODIFICAR:
                return formModify();
            case BAJA:
                return formDelete();
            case BUSCAR:
            default:
                return formFind();
        }
    }

    private VBox formDischarge() {
        TextField txtName = campoTexto("Nombre del producto");
        TextField txtStock = campoTexto("Stock inicial");

        Button btn = botonPrincipal("Agregar producto");
        btn.setOnAction(e -> {
            try {
                String name = txtName.getText();
                int stock = Integer.parseInt(txtStock.getText());

                AddProduct addProduct = context.getAddProductUseCase();
                addProduct.execute(name, stock);

                mostrarMensaje("Producto agregado correctamente.", false);
                txtName.clear();
                txtStock.clear();
            } catch (NumberFormatException ex) {
                mostrarMensaje("El stock debe ser un número entero.", true);
            } catch (IllegalArgumentException ex) {
                mostrarMensaje(ex.getMessage(), true);
            }
        });

        return new VBox(12, txtName, txtStock, btn);
    }

    private VBox formModify() {
        TextField txtId = campoTexto("ID del producto a modificar");
        TextField txtName = campoTexto("Nuevo nombre");
        TextField txtStock = campoTexto("Nuevo stock");

        Button btn = botonPrincipal("Guardar cambios");
        btn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                int stock = Integer.parseInt(txtStock.getText());

                UpdateProduct updateProduct = context.getUpdateProductUseCase();
                boolean actualizado = updateProduct.execute(name, stock, id);

                if (actualizado) {
                    mostrarMensaje("Producto actualizado correctamente.", false);
                } else {
                    mostrarMensaje("No se encontró un producto con ese ID.", true);
                }
            } catch (NumberFormatException ex) {
                mostrarMensaje("ID y stock deben ser números enteros.", true);
            }
        });

        return new VBox(12, txtId, txtName, txtStock, btn);
    }

    private VBox formDelete() {
        TextField txtId = campoTexto("ID del producto a eliminar");

        Button btn = botonPrincipal("Eliminar producto");
        btn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());

                DeleteProduct deleteProduct = context.getDeleteProductUseCase();
                boolean eliminado = deleteProduct.execute(id);

                if (eliminado) {
                    mostrarMensaje("Producto eliminado correctamente.", false);
                    txtId.clear();
                } else {
                    mostrarMensaje("No se encontró un producto con ese ID.", true);
                }
            } catch (NumberFormatException ex) {
                mostrarMensaje("El ID debe ser un número entero.", true);
            }
        });

        return new VBox(12, txtId, btn);
    }

    private VBox formFind() {
        TextField txtId = campoTexto("ID del producto a buscar");

        Button btn = botonPrincipal("Buscar");
        btn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());

                FindProduct findProduct = context.getFindProductUseCase();
                Optional<Product> resultado = findProduct.execute(id);

                if (resultado.isPresent()) {
                    Product p = resultado.get();
                    mostrarMensaje("Encontrado: " + p.getName() + " (stock: " + p.getStock() + ")", false);
                } else {
                    mostrarMensaje("No se encontró un producto con ese ID.", true);
                }
            } catch (NumberFormatException ex) {
                mostrarMensaje("El ID debe ser un número entero.", true);
            }
        });

        return new VBox(12, txtId, btn);
    }

    // ---------- Helpers de UI ----------

    private TextField campoTexto(String prompt) {
        TextField campo = new TextField();
        campo.setPromptText(prompt);
        campo.getStyleClass().add("field");
        return campo;
    }

    private Button botonPrincipal(String texto) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("btn-primary");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.setText(texto);
        lblMensaje.setVisible(true);
        lblMensaje.setManaged(true);
        lblMensaje.getStyleClass().removeAll("error-label", "success-label");
        lblMensaje.getStyleClass().add(esError ? "error-label" : "success-label");
    }

    private String tituloSegunModo() {
        switch (modo) {
            case ALTA: return "Dar de alta";
            case MODIFICAR: return "Modificar producto";
            case BAJA: return "Dar de baja";
            case BUSCAR: return "Buscar producto";
            default: return "Productos";
        }
    }

    private String subtituloSegunModo() {
        switch (modo) {
            case ALTA: return "Completá los datos del nuevo producto";
            case MODIFICAR: return "Ingresá el ID y los nuevos valores";
            case BAJA: return "Ingresá el ID del producto a eliminar";
            case BUSCAR: return "Ingresá el ID del producto a consultar";
            default: return "";
        }
    }
}