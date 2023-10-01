//package com.danila.javafxauth;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.util.Objects;
//
//public class NavigationManager {
//    private Stage primaryStage;
//    private static NavigationManager instance;
//
//    private NavigationManager() {
//        primaryStage = new Stage();
//    }
//
//    public static NavigationManager getNavigationManagerInstance(Stage primaryStage) {
//        if (instance == null) {
//            instance = new NavigationManager();
//        }
//        return instance;
//    }
//
//    public Stage getPrimaryStage() {
//        return primaryStage;
//    }
//
//    // Метод для перехода на страницу логирования
//    public void switchToLoginPage() {
//        try {
//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
//            primaryStage.setTitle("Вход");
//            primaryStage.setScene(new Scene(root, 600, 500));
//            primaryStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Метод для перехода на страницу регистрации
//    public void switchToRegistrationPage() {
//        try {
//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("registration.fxml")));
//            primaryStage.setTitle("Регистрация");
//            primaryStage.setScene(new Scene(root, 600, 500));
//            primaryStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Метод для перехода на страницу успешного входа
//    public void switchToSuccessPage() {
//        try {
//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("success_page.fxml")));
//            primaryStage.setTitle("Успех");
//            primaryStage.setScene(new Scene(root, 600, 500));
//            primaryStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Метод для перехода на страницу отказа в доступе
//    public void switchToAccessDeniedPage() {
//        try {
//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("access_denied_page.fxml")));
//            primaryStage.setTitle("Отказ");
//            primaryStage.setScene(new Scene(root, 600, 500));
//            primaryStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
