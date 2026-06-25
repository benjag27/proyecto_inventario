package org.phora.presentation;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.phora.application.LoginService;

public class LoginView {

  private final LoginService loginService;
  private final SceneManager sceneManager; // Lo necesitamos para cambiar de escena

  public LoginView(LoginService loginService, SceneManager sceneManager) {
    this.loginService = loginService;
    this.sceneManager = sceneManager;
  }

  public Scene crearScene() {
    TextField txtUser = new TextField();
    txtUser.setPromptText("Usuario");

    PasswordField txtPass = new PasswordField(); // ¡Usa PasswordField para ocultar la pass!
    txtPass.setPromptText("Contraseña");

    Button btnLogin = new Button("Ingresar");
    Label lblError = new Label();

    btnLogin.setOnAction(e -> {
      if (loginService.authenticate(txtUser.getText(), txtPass.getText())) {
        // Si es exitoso, le pedimos al sceneManager que cambie a la vista de inventario
        sceneManager.mostrarInventario();
      } else {
        lblError.setText("Usuario o contraseña incorrectos");
      }
    });

    VBox layout = new VBox(10, txtUser, txtPass, btnLogin, lblError);
    return new Scene(layout, 300, 200);
  }
}