package com.danila.javafxauth;

import com.danila.javafxauth.controllers.SuccessPageController;

import com.danila.javafxauth.model.User;
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

    public void switchToSuccessPage(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("success_page.fxml"));
            Parent root = loader.load();

            SuccessPageController controller = loader.getController();
            controller.setUser(user);
            controller.initialize(user);

            primaryStage.setTitle("Успех");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
