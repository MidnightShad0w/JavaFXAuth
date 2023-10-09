package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Main;
import com.danila.javafxauth.Utils;
import com.danila.javafxauth.database.DatabaseConnection;
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
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM \"user\" WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedUUID = resultSet.getString("uuid");
                if (storedUUID.equals(uuid)) {
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
