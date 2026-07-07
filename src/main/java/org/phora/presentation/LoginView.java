package org.phora.presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.phora.domain.service.LoginService;

/**
 * Pantalla de inicio de sesión. Solo construye su Scene y, ante un
 * login exitoso, le pide al SceneManager que navegue al menú principal.
 * No conoce el Stage directamente (mismo patrón que ProductView.java).
 */
public class LoginView {

  private final LoginService loginService;
  private final SceneManager sceneManager;

  private final Label lblError = new Label();

  public LoginView(LoginService loginService, SceneManager sceneManager) {
    this.loginService = loginService;
    this.sceneManager = sceneManager;
  }

  /** Ancho y alto fijos de la ventana de login — Main/SceneManager los usan para bloquear el resize. */
  public static final double WIDTH = 480;
  public static final double HEIGHT = 480;

  public Scene createScene() {
    Label titulo = new Label("Inventario");
    titulo.getStyleClass().add("login-title");

    Label subtitulo = new Label("Iniciá sesión para continuar");
    subtitulo.getStyleClass().add("login-subtitle");

    TextField txtUsername = new TextField();
    txtUsername.setPromptText("Usuario");
    txtUsername.getStyleClass().add("field");

    PasswordField txtPassword = new PasswordField();
    txtPassword.setPromptText("Contraseña");
    txtPassword.getStyleClass().add("field");

    lblError.getStyleClass().add("error-label");
    lblError.setVisible(false);
    lblError.setManaged(false); // no ocupa espacio mientras está oculto, evita saltos de layout

    Button btnLogin = new Button("Iniciar sesión");
    btnLogin.getStyleClass().add("btn-primary");
    btnLogin.setMaxWidth(Double.MAX_VALUE);
    btnLogin.setOnAction(e -> onLoginClick(txtUsername.getText(), txtPassword.getText()));

    // Enter en cualquiera de los dos campos también intenta loguear
    txtUsername.setOnAction(e -> btnLogin.fire());
    txtPassword.setOnAction(e -> btnLogin.fire());

    VBox encabezado = new VBox(6, titulo, subtitulo);
    encabezado.setAlignment(Pos.CENTER_LEFT);

    VBox campos = new VBox(12, txtUsername, txtPassword, lblError, btnLogin);
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

  private void onLoginClick(String username, String password) {
    if (username.isBlank() || password.isBlank()) {
      showError("Completá usuario y contraseña.");
      return;
    }

    boolean valido = loginService.authenticate(username, password);

    if (valido) {
      sceneManager.showMainMenu();
    } else {
      showError("Usuario o contraseña incorrectos.");
    }
  }

  private void showError(String mensaje) {
    lblError.setText(mensaje);
    lblError.setVisible(true);
  }
}