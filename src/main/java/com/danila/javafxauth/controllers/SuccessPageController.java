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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SuccessPageController {
    @FXML
    private TextArea fileContentTextArea;
    @FXML
    private Label fileNameLabel;
    private File selectedFile;
    private FileChannel fileChannel;
    private FileLock fileLock;
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

    private void openFileChannel() {
        if (selectedFile != null && selectedFile.exists()) {
            RandomAccessFile randomAccessFile;

            try {
                randomAccessFile = new RandomAccessFile(selectedFile, "rw");
                fileChannel = randomAccessFile.getChannel();

                fileLock = fileChannel.tryLock();

                if (fileLock != null) {
                    System.out.println("Файловый канал открыт");
                } else {
                    throw new IOException("Не удалось получить блокировку файла. Возможно, файл уже открыт другим процессом.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Не удалось получить блокировку файла" + e.getMessage());
                closeFileChannel();
            }
        } else {
            System.out.println("Файл не найден");
        }
    }


    private void writeToFileChannel(String contentToWrite) {
        if (fileChannel != null && fileChannel.isOpen()) {
            try {
                fileChannel.position(0);
                byte[] contentBytes = contentToWrite.getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(contentBytes);

                fileChannel.write(buffer);

                System.out.println("Файловый канал записан");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Не удалось записать в файловый канал" + e.getMessage());
            }
        } else {
            System.out.println("Файловый канал не открыт");
        }
    }

    private String readFromFileChannel() {
        if (fileChannel != null && fileChannel.isOpen()) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = fileChannel.read(buffer);
                buffer.flip();

                if (bytesRead > 0) {
                    byte[] readData = new byte[bytesRead];
                    buffer.get(readData);

                    return new String(readData);
                } else {
                    System.out.println("Файл пуст");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Не удалось прочитать файловый канал" + e.getMessage());
            }
        } else {
            System.out.println("Файловый канал не открыт");
        }
        return null;
    }

    private void closeFileChannel() {
        if (fileChannel != null && fileChannel.isOpen()) {
            try {
                if (fileLock != null) {
                    fileLock.release();
                }

                fileChannel.close();
                System.out.println("Файловый канал закрыт");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Не удалось закрыть файловый канал");
            }
        } else {
            System.out.println("Файловый канал не открыт");
        }
    }
    @FXML
    private void chooseFileButtonAction() throws IOException, SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите текстовый файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt", "*.secretext", "*.zip"));
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
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
            if (selectedFile.getAbsolutePath().endsWith(".zip")) {
                String newFilePath = selectedFile.getAbsolutePath().substring(0, selectedFile.getAbsolutePath().length() - 4);
                FileInfo currentFileInfo = FileInfoDao.getUserFileInfoByPath(newFilePath);
                String lastModifiedUserFilePassword = String.valueOf(currentFileInfo.getUserId());
                Utils.extractArchiveAndDeleteSource(selectedFile.getAbsolutePath(), lastModifiedUserFilePassword);
                selectedFile = new File(newFilePath);
            }
            openFileChannel();
            String fileContent = readFromFileChannel();
            if (validateFile(selectedFile, fileContent)) {
                fileNameLabel.setText(selectedFile.getName());
                fileContentTextArea.setText(fileContent);
            } else {
                fileNameLabel.setText("");
                fileContentTextArea.setText("");
                createAlertMessage(Alert.AlertType.ERROR, "Файл был изменён извне");
            }
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

    private void createAlertMessage(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void saveFileButtonAction() {
        if (user.getCredentials().equals("edit")) {
            try {
                String fileContent = fileContentTextArea.getText();
                writeToFileChannel(fileContent);
                FileInfo currentFileInfo = new FileInfo(Utils.generateHash(fileContent), LocalDateTime.now(), selectedFile.getAbsolutePath(), user.getId());
                closeFileChannel();
                if (FileInfoDao.setUserFile(currentFileInfo, user) > 0) {
                    createAlertMessage(Alert.AlertType.INFORMATION, "Файл успешно сохранён");
                    Utils.archiveFileAndDeleteSource(selectedFile.getAbsolutePath(), selectedFile.getName(), fileContent, String.valueOf(user.getId()), selectedFile.toPath());
                } else {
                    createAlertMessage(Alert.AlertType.ERROR, "Не удалось сохранить файл");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                createAlertMessage(Alert.AlertType.ERROR, "Произошла ошибка базы данных");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            createAlertMessage(Alert.AlertType.ERROR, "Недостаточно прав для редактирования файла");
            Main.getInstance().switchToLoginPage();
        }

    }


    @FXML
    private void backToLoginButtonAction() {
        Main.getInstance().switchToLoginPage();
    }
}

