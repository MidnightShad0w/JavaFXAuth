package com.danila.javafxauth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main extends Application {
    private static Main instance;
    private Stage primaryStage; // Для хранения ссылки на главное окно
    public static Main getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage; // Сохраняем ссылку на главное окно
        instance = this; // Сохраняем текущий экземпляр Main для вызова глобальных методов
        switchToLoginPage();
    }

    // Метод для получения UUID
    public String getUUID() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("wmic", "csproduct", "get", "UUID");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder uuidBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                uuidBuilder.append(line.trim());
            }

            return uuidBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод для перехода на страницу логирования
    public void switchToLoginPage() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
            primaryStage.setTitle("Вход");
            primaryStage.setScene(new Scene(root, 600, 500));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для перехода на страницу регистрации
    public void switchToRegistrationPage() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("registration.fxml")));
            primaryStage.setTitle("Регистрация");
            primaryStage.setScene(new Scene(root, 600, 500));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для перехода на страницу успешного входа
    public void switchToSuccessPage() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("success_page.fxml")));
            primaryStage.setTitle("Успех");
            primaryStage.setScene(new Scene(root, 600, 500));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Метод для перехода на страницу отказа в доступе
    public void switchToAccessDeniedPage() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("access_denied_page.fxml")));
            primaryStage.setTitle("Отказ");
            primaryStage.setScene(new Scene(root, 600, 500));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
