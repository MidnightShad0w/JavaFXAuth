package com.danila.javafxauth.controllers;

import com.danila.javafxauth.dao.UserDao;
import com.danila.javafxauth.model.User;
import javafx.fxml.FXML;
import com.danila.javafxauth.Main;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class SuccessPageController {
    @FXML
    public TextField messageTextField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label messageLabel;

    private User user;

    public void setUser(User user) {
        this.user = user;
    }
    public void initialize(User user) {
        usernameLabel.setText(user.getName());
        messageLabel.setText(user.getMessage());
    }

    @FXML
    private void sendMessageButtonAction() {
        String message = messageTextField.getText();
        try {
            UserDao.updateUserMessage(user, message);
            messageLabel.setText(message);
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("<--Произошла ошибка базы данных-->");
        }
    }

    @FXML
    private void backToLoginButtonAction() {
        Main.getInstance().switchToLoginPage();
    }
}

