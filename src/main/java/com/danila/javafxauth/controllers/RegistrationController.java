package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Main;

import com.danila.javafxauth.Utils;
import com.danila.javafxauth.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


    // Метод создания всплывающей подсказки
    private void showTooltip(Control control, String message, int durationMillis) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDuration(Duration.millis(durationMillis));

        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

        Tooltip.install(control, tooltip);
        tooltip.show(control.getScene().getWindow());
    }

    // Метод очистки всплывающей подсказки
    private void clearTooltip(Control control) {
        Tooltip.uninstall(control, null);
    }

    private boolean isEmailUnique(String email) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            preparedStatement.close();
            return count == 0; // Если count равен 0, email уникален
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Если произошла ошибка считаем email неуникальным
        }
    }

    // Метод для обработки нажатия кнопки "Зарегистрироваться"
    @FXML
    private void registrationButtonAction() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String uuid = Utils.getUUID();

        // Создаём диалоговое окно Alert для отображения сообщения
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);


        if (uuid == null) {
            alert.setTitle("Ошибка UUID");
            alert.setContentText("Не удалось получить UUID устройства");
            return;
        }

        // Проверка ввода имени
        if (name.isEmpty()) {
            showTooltip(nameField, "Введите имя", 1000);
            return;
        } else {
            clearTooltip(nameField);
        }

        // Проверка ввода телефона (допустимы только цифры)
        if (phone.isEmpty() || !phone.matches("\\d+")) {
            showTooltip(phoneField, "Номер телефона должен содержать только цифры", 100);
            return;
        } else {
            clearTooltip(phoneField);
        }

        // Проверка ввода email (валидная почта)
        if (email.isEmpty() || !email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
            showTooltip(emailField, "Пожалуйста, введите корректный адрес электронной почты", 1000);
            return;
        } else {
            clearTooltip(emailField);
        }

        // Проверка ввода пароля (буквы и цифры, минимум 6 знаков)
        if (password.isEmpty() || !password.matches("^[A-Za-z0-9]{6,}$")) {
            showTooltip(passwordField, "Пароль должен содержать буквы и цифры и быть не менее 6 символов", 1000);
            return;
        } else {
            clearTooltip(passwordField);
        }

        // Проверка на уникальность логина
        if (!isEmailUnique(email)) {
            alert.setTitle("Неверный email");
            alert.setContentText("Пользователь с таким email уже существует");
            alert.showAndWait();
            return;
        } else {
            clearTooltip(emailField);
        }

        try {
            // Создаем подключение к бд
            Connection connection = DatabaseConnection.getConnection();
            // SQL-запрос для вставки данных в таблицу
            String query = "INSERT INTO users (name, phone, email, password, uuid) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
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

