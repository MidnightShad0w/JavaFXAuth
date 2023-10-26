package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Main;

import com.danila.javafxauth.Utils;
import com.danila.javafxauth.dao.UserDao;
import com.danila.javafxauth.database.DatabaseConnection;
import com.danila.javafxauth.exceptions.InvalidUserException;
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

    private void showTooltip(Control control, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDuration(Duration.millis(2000));
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
        Tooltip.install(control, tooltip);
        tooltip.show(control.getScene().getWindow());

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
            return UserDao.checkUserEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @FXML
    private void registrationButtonAction() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String hashedPassword = Utils.generateHash(password);
        String uuid = Utils.getUUID();
        String credentials = "view";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);

        var user = new User(name, phone, email, hashedPassword, uuid, null, null, credentials);

        try {
            validateUser(user, password);
        } catch (InvalidUserException ex) {
            if (ex.getMessage() == null) {
                return;
            } else if (!ex.getMessage().isEmpty()) {
                alert.setTitle(ex.getTitle());
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
                return;
            }
        }

        try {
            if (UserDao.createUser(user) > 0) {
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

    private void  validateUser(User user, String password) throws InvalidUserException {
        String title = "";
        String message = "";

        if (user.getUuid() == null) {
            title = "Ошибка UUID";
            message = "Не удалось получить UUID устройства";
        }

        if (user.getName().isEmpty()) {
            showTooltip(nameField, "Введите имя");
            throw new InvalidUserException();
        }

        if (user.getPhone().isEmpty() || !user.getPhone().matches("\\d+")) {
            showTooltip(phoneField, "Номер телефона должен содержать только цифры");
            throw new InvalidUserException();
        }

        if (user.getEmail().isEmpty() || !user.getEmail().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
            showTooltip(emailField, "Пожалуйста, введите корректный адрес электронной почты");
            throw new InvalidUserException();
        }

        if (user.getPassword().isEmpty() || !password.matches("^[A-Za-z0-9]{6,}$")) {
            showTooltip(passwordField, "Пароль должен содержать буквы и цифры и быть не менее 6 символов");
            throw new InvalidUserException();
        }

        if (!isEmailUnique(user.getEmail())) {
            title = "Неверный email";
            message = "Пользователь с таким email уже существует";
        }

        throw new InvalidUserException(title, message);
    }

    @FXML
    private void backToLoginButtonAction() {
        Main.getInstance().switchToLoginPage();
    }
}

