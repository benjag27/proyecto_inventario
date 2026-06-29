package org.phora.presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.phora.infrastructure.AppContext;

/**
 * Menú principal post-login: una tarjeta-botón por funcionalidad.
 * Cada tarjeta navega a su propia vista a través del SceneManager.
 * Las funcionalidades que todavía no están implementadas muestran
 * un aviso "Próximamente" en vez de romper la navegación.
 */
public class MenuPrincipalView {

    private final AppContext context;
    private final SceneManager sceneManager;

    public MenuPrincipalView(AppContext context, SceneManager sceneManager) {
        this.context = context;
        this.sceneManager = sceneManager;
    }

    public Scene crearScene() {
        Label titulo = new Label("Panel de inventario");
        titulo.getStyleClass().add("menu-header");

        Label subtitulo = new Label("Elegí qué querés gestionar");
        subtitulo.getStyleClass().add("menu-subheader");

        VBox encabezado = new VBox(4, titulo, subtitulo);

        Hyperlink logout = new Hyperlink("Cerrar sesión");
        logout.getStyleClass().add("logout-link");
        logout.setOnAction(e -> sceneManager.mostrarLogin());

        HBox top = new HBox(encabezado);
        HBox.setHgrow(encabezado, javafx.scene.layout.Priority.ALWAYS);
        top.getChildren().add(logout);
        top.setAlignment(Pos.TOP_RIGHT);
        top.setPadding(new Insets(0, 0, 28, 0));

        GridPane grilla = new GridPane();
        grilla.setHgap(18);
        grilla.setVgap(18);

        grilla.add(crearTarjeta("📦", "Productos", "Alta, baja y stock", this::abrirProductos), 0, 0);
        grilla.add(crearTarjeta("🏷️", "Categorías", "Organizá tus rubros", this::proximamente), 1, 0);
        grilla.add(crearTarjeta("🔄", "Movimientos", "Entradas y salidas", this::proximamente), 0, 1);
        grilla.add(crearTarjeta("👤", "Usuarios", "Accesos del equipo", this::proximamente), 1, 1);

        VBox contenido = new VBox(top, grilla);
        contenido.setPadding(new Insets(40));

        BorderPane root = new BorderPane(contenido);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    private VBox crearTarjeta(String icono, String titulo, String descripcion, Runnable accion) {
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

    private void abrirProductos() {
        sceneManager.mostrarInventario();
    }

    private void proximamente() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Esta sección todavía no está disponible.");
        alert.setHeaderText("Próximamente");
        alert.showAndWait();
    }
}