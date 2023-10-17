package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Main;
import com.danila.javafxauth.Utils;
import com.danila.javafxauth.dao.UserDao;
import com.danila.javafxauth.exceptions.InvalidAuthorizationException;
import com.danila.javafxauth.model.User;
import javafx.fxml.FXML;
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
            validateUserAuthorization(checkingUser, password, uuid);
            messageLabel.setText("Вход выполнен успешно");
            UserDao.updateUserBlockingTime(checkingUser, null);
            loginAttempts = 0;
            Main.getInstance().switchToSuccessPage(checkingUser);
        } catch (InvalidAuthorizationException e) {
            messageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Ошибка базы данных");
        }
    }

    private void validateUserAuthorization(User checkingUser, String password, String uuid) throws InvalidAuthorizationException {

        if (checkingUser == null) {
            throw new InvalidAuthorizationException("Неверный логин");
        }

        if (checkingUser.getBlockingTime() != null &&
                checkingUser.getBlockingTime()
                        .plusSeconds(10)
                        .isAfter(LocalDateTime.now())) {
            loginAttempts = 0;
            throw new InvalidAuthorizationException("Нужно подождать пока не истечет время таймера");
        }

        try {
            if (loginAttempts >= 3) {
                UserDao.updateUserBlockingTime(checkingUser, LocalDateTime.now());
                throw new InvalidAuthorizationException("Превышено количество попыток ввода пароля, установлен таймер");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidAuthorizationException("Ошибка базы данных");
        }

        if (!Utils.checkPassword(password, checkingUser.getPassword())) {
            loginAttempts++;
            throw new InvalidAuthorizationException("Неверный пароль");
        }

        if (!checkingUser.getUuid().equals(uuid)) {
            Main.getInstance().switchToAccessDeniedPage();
            throw new InvalidAuthorizationException("Доступ запрещен");
        }
    }


    @FXML
    private void registrationButtonAction() {
        Main.getInstance().switchToRegistrationPage();
    }

}
