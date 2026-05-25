package org.example.music;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

public class registerController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Валидация
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            updateStatus("Заполните все поля!", Color.RED);
            return;
        }

        if (username.length() < 3) {
            updateStatus("Логин должен содержать минимум 3 символа!", Color.RED);
            return;
        }

        if (password.length() < 6) {
            updateStatus("Пароль должен содержать минимум 6 символов!", Color.RED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            updateStatus("Пароли не совпадают!", Color.RED);
            return;
        }

        // Регистрация
        boolean success = DatabaseHandler.registerUser(username, password);

        if (success) {
            updateStatus("Регистрация успешна! ✅", Color.GREEN);

            // Автоматический переход через 1.2 секунды
            new Thread(() -> {
                try {
                    Thread.sleep(1200);
                    javafx.application.Platform.runLater(this::switchToLogin);
                } catch (InterruptedException e) {
                    switchToLogin();
                }
            }).start();
        } else {
            updateStatus("Ошибка: пользователь с таким логином уже существует!", Color.RED);
        }
    }

    private void switchToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth-view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setScene(scene);
            stage.setTitle("SoundWave - Вход");
            stage.show();
        } catch (IOException e) {
            System.err.println("Ошибка перехода: " + e.getMessage());
            updateStatus("Не удалось перейти к окну входа", Color.RED);
        }
    }

    @FXML
    private void handleBackToLogin() {
        switchToLogin();
    }

    private void updateStatus(String message, Color color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setTextFill(color);
        }
    }
}