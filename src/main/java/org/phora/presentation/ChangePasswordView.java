package org.phora.presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.phora.domain.service.LoginService;

/**
 * Pantalla de cambio de contraseña obligatoria tras el primer inicio de
 * sesión con el administrador de la base de distribución. Mismo patrón que
 * LoginView: construye su Scene y delega la navegación al SceneManager.
 */
public class ChangePasswordView {

  private final LoginService loginService;
  private final SceneManager sceneManager;
  private final String username;

  private final Label lblError = new Label();

  public ChangePasswordView(LoginService loginService, SceneManager sceneManager, String username) {
    this.loginService = loginService;
    this.sceneManager = sceneManager;
    this.username = username;
  }

  public static final double WIDTH = 480;
  public static final double HEIGHT = 480;

  public Scene createScene() {
    Label titulo = new Label("Cambiar contraseña");
    titulo.getStyleClass().add("login-title");

    Label subtitulo = new Label("Definí una contraseña nueva para tu cuenta");
    subtitulo.getStyleClass().add("login-subtitle");

    PasswordField txtNueva = new PasswordField();
    txtNueva.setPromptText("Contraseña nueva");
    txtNueva.getStyleClass().add("field");

    PasswordField txtRepeticion = new PasswordField();
    txtRepeticion.setPromptText("Repetir contraseña");
    txtRepeticion.getStyleClass().add("field");

    lblError.getStyleClass().add("error-label");
    lblError.setVisible(false);
    lblError.setManaged(false); // no ocupa espacio mientras está oculto, evita saltos de layout

    Button btnGuardar = new Button("Guardar contraseña");
    btnGuardar.getStyleClass().add("btn-primary");
    btnGuardar.setMaxWidth(Double.MAX_VALUE);
    btnGuardar.setOnAction(e -> onGuardarClick(txtNueva.getText(), txtRepeticion.getText()));

    // Enter en cualquiera de los dos campos también intenta guardar
    txtNueva.setOnAction(e -> btnGuardar.fire());
    txtRepeticion.setOnAction(e -> btnGuardar.fire());

    VBox encabezado = new VBox(6, titulo, subtitulo);
    encabezado.setAlignment(Pos.CENTER_LEFT);

    VBox campos = new VBox(12, txtNueva, txtRepeticion, lblError, btnGuardar);
    campos.setPadding(new Insets(28, 0, 0, 0));

    VBox contenido = new VBox(encabezado, campos);
    contenido.getStyleClass().add("login-card");
    contenido.setPadding(new Insets(40));
    contenido.setMaxWidth(WIDTH);
    contenido.setMinWidth(HEIGHT);

    StackPane root = new StackPane(contenido);
    root.getStyleClass().add("root");

    Scene scene = new Scene(root, WIDTH, HEIGHT);
    scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
    return scene;
  }

  private void onGuardarClick(String nueva, String repeticion) {
    if (nueva.isBlank() || repeticion.isBlank()) {
      showError("Completá ambos campos.");
      return;
    }

    if (!nueva.equals(repeticion)) {
      showError("Las contraseñas no coinciden.");
      return;
    }

    if (loginService.authenticate(username, nueva)) {
      showError("La contraseña nueva debe ser distinta de la actual.");
      return;
    }

    if (!loginService.changePassword(username, nueva)) {
      showError("La contraseña debe tener al menos 6 caracteres.");
      return;
    }

    sceneManager.showMainMenu();
  }

  private void showError(String mensaje) {
    lblError.setText(mensaje);
    lblError.setVisible(true);
  }
}
