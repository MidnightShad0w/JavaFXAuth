package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Main;
import com.danila.javafxauth.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Button registrationButton;

    @FXML
    private void loginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String uuid = Main.getInstance().getUUID();

        try {
            Connection connection = DatabaseConnection.getConnection(); // Получение соединения с базой данных
            String sql = "SELECT * FROM users WHERE name = ? AND password = ? AND uuid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                messageLabel.setText("Вход выполнен успешно");
                Main.getInstance().switchToSuccessPage();
            } else {
                messageLabel.setText("Неверный логин или пароль");
                Main.getInstance().switchToAccessDeniedPage();
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
