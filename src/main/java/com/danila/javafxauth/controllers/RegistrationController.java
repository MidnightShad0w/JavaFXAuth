package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Main;

import com.danila.javafxauth.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registrationButton;

    //TODO unique email check
    //TODO dokerize db and app

    private void showTooltip(Control control, String message, int durationMillis) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDuration(Duration.millis(durationMillis));

        // Устанавливаем позицию AnchorLocation для Tooltip
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

        Tooltip.install(control, tooltip);
        tooltip.show(control.getScene().getWindow());
    }

    private void clearTooltip(Control control) {
        Tooltip.uninstall(control, null);
    }

    // Метод для обработки нажатия кнопки "Зарегистрироваться"
    @FXML
    private void registrationButtonAction() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String uuid = Main.getInstance().getUUID();

        // Создаём диалоговое окно Alert для отображения сообщения
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);


        // Проверка ввода имени
        if (name.isEmpty()) {
            showTooltip(nameField, "Введите имя", 1000);
            return;
        } else {
            clearTooltip(nameField);
        }

        // Проверка ввода телефона (допустимы только цифры)
        if (phone.isEmpty() || !phone.matches("\\d+")) {
            showTooltip(phoneField, "Номер телефона должен содержать только цифры.", 100);
            return;
        } else {
            clearTooltip(phoneField);
        }

        // Проверка ввода email (валидная почта)
        if (email.isEmpty() || !email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
            showTooltip(emailField, "Пожалуйста, введите корректный адрес электронной почты.", 1000);
            return;
        } else {
            clearTooltip(emailField);
        }

        // Проверка ввода пароля (буквы и цифры, минимум 6 знаков)
        if (password.isEmpty() || !password.matches("^[A-Za-z0-9]{6,}$")) {
            showTooltip(passwordField, "Пароль должен содержать буквы и цифры и быть не менее 6 символов.", 1000);
            return;
        } else {
            clearTooltip(passwordField);
        }


        try {
            // Создаем подключение к бд
            Connection connection = DatabaseConnection.getConnection();
            // SQL-запрос для вставки данных в таблицу
            String insertQuery = "INSERT INTO users (name, phone, email, password, uuid) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, uuid);

            // Выполнение запроса на вставку
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                // Если вставка прошла успешно
                alert.setTitle("Успешная регистрация");
                alert.setContentText("Регистрация выполнена успешно!");
                Main.getInstance().switchToLoginPage();
            } else {
                alert.setTitle("Запись не добавлена в бд");
                alert.setContentText("Не удалось выполнить регистрацию.");
            }

            preparedStatement.close();
//            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            alert.setTitle("Ошибка SQL");
            alert.setContentText("Произошла ошибка при регистрации.");
        } finally {
            alert.showAndWait();
        }
    }

    // Метод для обработки нажатия кнопки "Вернуться"
    @FXML
    private void backToLoginButtonAction() {
        Main.getInstance().switchToLoginPage();
    }
}

