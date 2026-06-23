package org.phora.presentation;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.phora.application.AddProduct;

public class ProductView {

    private final AddProduct addProductUseCase;

    // Recibe el caso de uso desde el Main
    public ProductView(AddProduct addProductUseCase) {
        this.addProductUseCase = addProductUseCase;
    }

    public void start(Stage stage) {
        TextField txtName = new TextField();
        txtName.setPromptText("Nombre del producto");

        TextField txtStock = new TextField();
        txtStock.setPromptText("Stock");

        Button btnAdd = new Button("Agregar Producto");

        btnAdd.setOnAction(e -> {
            try {
                String name = txtName.getText();
                int stock = Integer.parseInt(txtStock.getText());

                // Se ejecuta el caso de uso limpiamente
                this.addProductUseCase.execute(name, stock);

                txtName.clear();
                txtStock.clear();
            } catch (NumberFormatException ex) {
                System.err.println("Por favor ingresa un número válido en el stock.");
            }
        });

        VBox layout = new VBox(10, txtName, txtStock, btnAdd);
        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Gestión de Inventario");
        stage.show();
    }
}