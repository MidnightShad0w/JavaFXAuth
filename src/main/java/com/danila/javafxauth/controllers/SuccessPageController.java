package com.danila.javafxauth.controllers;

import com.danila.javafxauth.dao.UserDao;
import com.danila.javafxauth.model.User;
import javafx.fxml.FXML;
import com.danila.javafxauth.Main;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

public class SuccessPageController {
    @FXML
    private Button chooseFileButton;
    @FXML
    private TextArea fileContentTextArea;
    @FXML
    private Button saveFileButton;
    @FXML
    private Label fileNameLabel;
    private File selectedFile;
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
    private void chooseFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите текстовый файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                fileNameLabel.setText(selectedFile.getName());
                String fileContent = Files.readString(selectedFile.toPath());
                fileContentTextArea.setText(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveFileButtonAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        if (selectedFile != null) {
            if (user.getCredentials().equals("edit")){
                try {
                    String fileContent = fileContentTextArea.getText();
                    Files.writeString(selectedFile.toPath(), fileContent);
                    alert.setTitle("Успех");
                    alert.setContentText("Файл успешно сохранён");
                    alert.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                    alert.setTitle("Ошибка");
                    alert.setContentText("Ошибка при сохранении файла");
                    alert.showAndWait();
                }
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Отказано в доступе");
                alert.setContentText("Вы не имеете достаточно прав для редактирования файла");
                alert.showAndWait();
                Main.getInstance().switchToLoginPage();
            }
        }
    }

    @FXML
    private void backToLoginButtonAction() {
        Main.getInstance().switchToLoginPage();
    }
}

