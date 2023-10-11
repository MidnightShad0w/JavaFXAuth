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

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void loginButtonAction() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String uuid = Utils.getUUID();

        try {
            User checkingUser = UserDao.getUser(new User(email, password, uuid));
            if (checkingUser != null) {
                if (checkingUser.getUuid().equals(uuid)) {
                    messageLabel.setText("Вход выполнен успешно");
                    Main.getInstance().switchToSuccessPage();
                } else {
                    messageLabel.setText("Доступ запрещен");
                    Main.getInstance().switchToAccessDeniedPage();
                }
            } else {
                messageLabel.setText("Неверный логин или пароль");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void registrationButtonAction() {
        Main.getInstance().switchToRegistrationPage();
    }

}
