package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Main;
import com.danila.javafxauth.Utils;
import com.danila.javafxauth.dao.UserDao;
import com.danila.javafxauth.database.DatabaseConnection;
import com.danila.javafxauth.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.*;
import java.time.LocalDateTime;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private int loginAttempts = 0;

    @FXML
    private void loginButtonAction() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String uuid = Utils.getUUID();

        try {
            User checkingUser = UserDao.getUserByEmail(email);
            if (checkingUser != null) {
                // Проверяем, не заблокирован ли пользователь
                if (checkingUser.getBlockingTime() == null || checkingUser.getBlockingTime().plusSeconds(20).isBefore(LocalDateTime.now())) {
                    // Если пользователь не заблокирован или блокировка истекла
                    if (Utils.checkPassword(password, checkingUser.getPassword())) {
                        // Если пароль верен
                        if (checkingUser.getUuid().equals(uuid)) {
                            messageLabel.setText("Вход выполнен успешно");
                            UserDao.updateUserBlockingTime(checkingUser, null);
                            loginAttempts = 0;
                            Main.getInstance().switchToSuccessPage(checkingUser);
                        } else {
                            messageLabel.setText("Доступ запрещен");
                            Main.getInstance().switchToAccessDeniedPage();
                        }
                    } else {
                        // Неверный пароль
                        messageLabel.setText("Неверный пароль");
                        loginAttempts++;
                    }
                } else {
                    // Пользователь заблокирован
                    messageLabel.setText("Превышено количество попыток ввода пароля");
                }
            } else {
                // Неверный логин
                messageLabel.setText("Неверный логин");
            }

            if (loginAttempts >= 3) {
                // Если превышено количество попыток, блокируем пользователя
                UserDao.updateUserBlockingTime(checkingUser, LocalDateTime.now());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Ошибка базы данных");
        }
    }


    @FXML
    private void registrationButtonAction() {
        Main.getInstance().switchToRegistrationPage();
    }

}
