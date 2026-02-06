package com.alv.mastertools.controllers;

import java.io.IOException;

import com.alv.mastertools.App;
import com.alv.mastertools.models.Sesion;
import com.alv.mastertools.models.Usuario;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.alv.mastertools.services.TrackerService;

public class LoginController {
    @FXML
    private TextField userField;

    @FXML
    private PasswordField passField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() throws IOException {
        String inputUser = userField.getText();
        String inputPass = passField.getText();

        // Obtener credenciales guardadas
        String storedUser = TrackerService.get().getSettings().getUsername();
        String storedPass = TrackerService.get().getSettings().getPassword();

        // VALIDACIÓN DINÁMICA
        if (storedUser.equals(inputUser) && storedPass.equals(inputPass)) {

            // Pasamos 'password' (lo que escribió el usuario) al constructor
            Usuario usuarioDto = new Usuario(inputUser, "Administrador", inputPass);

            Sesion.get().setUsuarioLogueado(usuarioDto);

            // Auto-arranque de tracker si está configurado
            if (TrackerService.get().getSettings().isAutoStartOnLogin()) {
                TrackerService.get().startTracking();
            }

            // Navegación exitosa a la vista principal
            try {
                App.setRoot("primary");
            } catch (IOException e) {
                errorLabel.setText("Error al cargar la vista principal.");
                e.printStackTrace();
            }

        } else {
            // ERROR
            errorLabel.setText("Usuario o contraseña incorrectos");
        }
    }

    @FXML
    public void initialize() {
        // Lógica de UX: Comportamiento del ENTER

        // Cuando das ENTER en el campo Usuario...
        userField.setOnAction(event -> {
            // ... saltamos al campo Password
            passField.requestFocus();
        });

        // Nota: No necesitamos configurar passField porque,
        // como pusimos el botón como "defaultButton=true" en el FXML,
        // JavaFX ya sabe que ENTER ahí dispara el botón.
    }
}
