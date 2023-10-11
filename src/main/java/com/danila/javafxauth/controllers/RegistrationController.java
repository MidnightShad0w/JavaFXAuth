package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Main;

import com.danila.javafxauth.Utils;
import com.danila.javafxauth.dao.UserDao;
import com.danila.javafxauth.database.DatabaseConnection;
import com.danila.javafxauth.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    // Метод создания всплывающей подсказки
    private void showTooltip(Control control, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDuration(Duration.millis(2000));
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
        Tooltip.install(control, tooltip);
        tooltip.show(control.getScene().getWindow());

        // Создаем задержку для скрытия tooltip
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tooltip.hide();
            }
        }));
        timeline.play();
    }

    private boolean isEmailUnique(String email) {
        try {
            int count = UserDao.checkUserEmail(email);
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
            showTooltip(nameField, "Введите имя");
            return;
        }

        // Проверка ввода телефона (допустимы только цифры)
        if (phone.isEmpty() || !phone.matches("\\d+")) {
            showTooltip(phoneField, "Номер телефона должен содержать только цифры");
            return;
        }

        // Проверка ввода email (валидная почта)
        if (email.isEmpty() || !email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
            showTooltip(emailField, "Пожалуйста, введите корректный адрес электронной почты");
            return;
        }

        // Проверка ввода пароля (буквы и цифры, минимум 6 знаков)
        if (password.isEmpty() || !password.matches("^[A-Za-z0-9]{6,}$")) {
            showTooltip(passwordField, "Пароль должен содержать буквы и цифры и быть не менее 6 символов");
            return;
        }

        // Проверка на уникальность логина
        if (!isEmailUnique(email)) {
            alert.setTitle("Неверный email");
            alert.setContentText("Пользователь с таким email уже существует");
            alert.showAndWait();
            return;
        }

        try {
            if (UserDao.createUser(new User(name, phone, email, password, uuid)) > 0) {
                // Если вставка прошла успешно
                alert.setTitle("Успешная регистрация");
                alert.setContentText("Регистрация выполнена успешно!");
                Main.getInstance().switchToLoginPage();
            } else {
                alert.setTitle("Запись не добавлена в бд");
                alert.setContentText("Не удалось выполнить регистрацию.");
            }

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

