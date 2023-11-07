package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Utils;
import com.danila.javafxauth.dao.FileInfoDao;
import com.danila.javafxauth.dao.UserDao;
import com.danila.javafxauth.model.User;
import com.danila.javafxauth.model.FileInfo;
import javafx.fxml.FXML;
import com.danila.javafxauth.Main;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SuccessPageController {
    @FXML
    private TextArea fileContentTextArea;
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt", "*.secretext"));
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                String fileName = selectedFile.getName();
                if (fileName.toLowerCase().endsWith(".txt")) {
                    fileName = fileName.substring(0, fileName.length() - 4);
                    String newFileName = fileName + ".secretext";
                    File changingFile = new File(selectedFile.getParent(), newFileName);
                    boolean isRenamed = selectedFile.renameTo(changingFile);
                    if (isRenamed) {
                        selectedFile = changingFile;
                    } else {
                        throw new IOException("Ошибка переименования файла");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (validateFile(selectedFile)) {
                try {
                    fileNameLabel.setText(selectedFile.getName());
                    String fileContent = Files.readString(selectedFile.toPath());
                    fileContentTextArea.setText(fileContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                fileNameLabel.setText("");
                fileContentTextArea.setText("");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Файл был изменён извне");
                alert.showAndWait();
            }
        }
    }

    private boolean validateFile(File selectedFile) {
        try {
            FileInfo checkingFileInfo = FileInfoDao.getUserFileInfo(user.getEmail(), selectedFile);
            if (checkingFileInfo == null) {
                return true;
            }
            return Utils.checkHash(Files.readString(selectedFile.toPath()), checkingFileInfo.getFileHash());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void saveFileButtonAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);

        if (selectedFile != null) {
            if (user.getCredentials().equals("edit")) {
                try {
                    String fileContent = fileContentTextArea.getText();
                    Files.writeString(selectedFile.toPath(), fileContent);
                    FileInfo currentFileInfo = new FileInfo(Utils.generateHash(fileContent), LocalDateTime.now(), selectedFile.getAbsolutePath(), user.getId());
                    if (FileInfoDao.setUserFile(currentFileInfo) > 0) {
                        alert.setTitle("Успех");
                        alert.setContentText("Файл успешно сохранён");
                        alert.showAndWait();
                    } else {
                        alert.setTitle("Ошибка");
                        alert.setContentText("Ошибка при сохранении файла");
                        alert.showAndWait();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    alert.setTitle("Ошибка");
                    alert.setContentText("Ошибка при сохранении файла");
                    alert.showAndWait();
                } catch (SQLException e) {
                    e.printStackTrace();
                    alert.setTitle("Ошибка");
                    alert.setContentText("Ошибка базы данных");
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

