package org.phora.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.phora.domain.model.AuditLog;
import org.phora.infrastructure.AppContext;

import java.util.List;

public class AuditLogView {

    private final AppContext context;
    private final SceneManager sceneManager;

    public AuditLogView(AppContext context, SceneManager sceneManager) {
        this.context = context;
        this.sceneManager = sceneManager;
    }

    public Scene createScene() {
        Label titulo = new Label("Historial de Movimientos (Audit Log)");
        titulo.getStyleClass().add("menu-header");

        Button btnVolver = new Button("⬅ Volver");
        btnVolver.getStyleClass().add("logout-link"); // Reutiliza estilo de link o botón secundario
        btnVolver.setOnAction(e -> sceneManager.showMainMenu());

        HBox top = new HBox(titulo);
        HBox.setHgrow(titulo, javafx.scene.layout.Priority.ALWAYS);
        top.getChildren().add(btnVolver);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 0, 20, 0));

        // --- Configuración de la Tabla de Auditoría ---
        TableView<AuditLog> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AuditLog, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(80);
        colId.setMinWidth(50);

        TableColumn<AuditLog, String> colUser = new TableColumn<>("Usuario");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUser.setMinWidth(120);

        TableColumn<AuditLog, String> colAccion = new TableColumn<>("Acción");
        colAccion.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        colAccion.setMinWidth(150);

        TableColumn<AuditLog, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setMinWidth(400);

        TableColumn<AuditLog, String> colFecha = new TableColumn<>("Fecha / Hora");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colFecha.setMinWidth(180);

        tabla.getColumns().addAll(colId, colUser, colAccion, colDesc, colFecha);

        // --- Carga de Datos desde el Servicio ---
        List<AuditLog> logsDb = context.getAuditLogService().getAllLogs();
        ObservableList<AuditLog> datosTabla = FXCollections.observableArrayList(logsDb);
        tabla.setItems(datosTabla);

        VBox contenido = new VBox(15, top, tabla);
        contenido.setPadding(new Insets(40));
        VBox.setVgrow(tabla, javafx.scene.layout.Priority.ALWAYS);

        BorderPane root = new BorderPane(contenido);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 1420, 840);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }
}