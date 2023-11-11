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
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.zip.ZipException;

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
    private FileChannel fileChannel; // Переменная для хранения FileChannel
    private FileLock fileLock; // Переменная для хранения FileLock

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt", "*.secretext", "*.zip"));
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String fileContent = "";
            String fileName = selectedFile.getName();
            try {
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
                if (selectedFile.getAbsolutePath().endsWith(".zip")) {
                    fileName = selectedFile.getName();
                    fileName = fileName.substring(0, fileName.length() - 4);
                    int lastModifiedUserFilePassword = FileInfoDao.getUserFileInfoByPath(selectedFile.getAbsolutePath()).getUserId();
                    fileContent = extractFileContentFromZip(selectedFile.getAbsolutePath(), fileName, lastModifiedUserFilePassword);
                } else {
                    fileContent = new String(Files.readAllBytes(selectedFile.toPath()));
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
            if (validateFile(selectedFile, fileContent)) {
                fileNameLabel.setText(selectedFile.getName());
                fileContentTextArea.setText(fileContent);
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
    private void releaseFileLock() {
        if (fileLock != null) {
            try {
                fileLock.release();
            } catch (IOException e) {
                // Обработка ошибок
            }
            fileLock = null;
        }
        if (fileChannel != null) {
            try {
                fileChannel.close();
            } catch (IOException e) {
                // Обработка ошибок
            }
            fileChannel = null;
        }
    }

    // Метод для блокировки выбранного файла
    private void lockSelectedFile() {
        if (selectedFile != null) {
            try {
                fileChannel = new RandomAccessFile(selectedFile, "rw").getChannel();
                fileLock = fileChannel.lock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String extractFileContentFromZip(String zipFilePath, String entryName, Integer password) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);

            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toString().toCharArray());
            }

            FileHeader fileHeader = zipFile.getFileHeader(entryName);

            if (fileHeader != null) {
                InputStream inputStream = zipFile.getInputStream(fileHeader);
                String fileContent = new String(inputStream.readAllBytes());
                inputStream.close();
                return fileContent;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Ошибка открытия файла");
            alert.showAndWait();
            return null;
        }
    }

    private boolean validateFile(File selectedFile, String fileContent) {
        try {
            FileInfo checkingFileInfo = FileInfoDao.getUserFileInfoByPath(selectedFile.getAbsolutePath());
            if (checkingFileInfo == null) {
                return true;
            }
            return Utils.checkHash(fileContent, checkingFileInfo.getFileHash());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private void updateFileContentInZip(String zipFilePath, String entryName, String fileContent, Integer password) throws IOException {
        try {
            String subEntryName = entryName.substring(0, entryName.length() - 4);
            ZipFile zipFile = new ZipFile(zipFilePath);
            if (password != null) {
                zipFile.setPassword(password.toString().toCharArray());
            }

            ZipParameters parameters = new ZipParameters();
            parameters.setFileNameInZip(subEntryName);


            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
            zipFile.addStream(inputStream, parameters);
            zipFile.close();
        } catch (ZipException e) {
            e.printStackTrace();
            throw new IOException("Ошибка при записи в архив", e);
        }
    }

    @FXML
    private void saveFileButtonAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);

        if (user.getCredentials().equals("edit")) {
            try {
                String fileContent = fileContentTextArea.getText();
                FileInfo currentFileInfo;
                if (!selectedFile.getName().endsWith(".zip")) {
                    Files.writeString(selectedFile.toPath(), fileContent);
                    Utils.zipFile(selectedFile.getAbsolutePath(), selectedFile.getName(), fileContent, user.getId(), selectedFile.toPath());
                    currentFileInfo = new FileInfo(Utils.generateHash(fileContent), LocalDateTime.now(), selectedFile.getAbsolutePath() + ".zip", user.getId());
                } else {
                    Utils.zipFile(selectedFile.getAbsolutePath(), selectedFile.getName(), fileContent, user.getId(), selectedFile.toPath());
                    currentFileInfo = new FileInfo(Utils.generateHash(fileContent), LocalDateTime.now(), selectedFile.getAbsolutePath(), user.getId());
                }

                if (FileInfoDao.setUserFile(currentFileInfo, user) > 0) {
                    alert.setTitle("Успех");
                    alert.setContentText("Файл успешно сохранён");
                    alert.showAndWait();
                } else {
                    alert.setTitle("Ошибка");
                    alert.setContentText("Ошибка при сохранении файла");
                    alert.showAndWait();
                }
            } catch (ZipException e) {
                e.printStackTrace();
                alert.setTitle("Ошибка");
                alert.setContentText("Не удалось архивировать файл");
                alert.showAndWait();
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


    @FXML
    private void backToLoginButtonAction() {
        Main.getInstance().switchToLoginPage();
    }
}

