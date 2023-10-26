//package com.danila.javafxauth;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.util.Objects;
//
//public class NavigationManager {
//    private Stage stage;
//    private Scene scene;
//    private Parent root;
//    private static NavigationManager instance;
//
//    private NavigationManager() {
//        stage = new Stage();
//    }
//
//    public static NavigationManager getNavigationManagerInstance(Stage primaryStage) {
//        if (instance == null) {
//            instance = new NavigationManager();
//        }
//        return instance;
//    }
//
//    public Stage getStage() {
//        return stage;
//    }
//
//    // Метод для перехода на страницу логирования
//    public void switchToLoginPage(ActionEvent event) throws IOException {
//        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    // Метод для перехода на страницу регистрации
//    public void switchToRegistrationPage(ActionEvent event) throws IOException {
//        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("registration.fxml")));
//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    // Метод для перехода на страницу успешного входа
//    public void switchToSuccessPage() {
//        try {
//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("success_page.fxml")));
//            stage.setTitle("Успех");
//            stage.setScene(new Scene(root, 600, 500));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Метод для перехода на страницу отказа в доступе
//    public void switchToAccessDeniedPage() {
//        try {
//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("access_denied_page.fxml")));
//            stage.setTitle("Отказ");
//            stage.setScene(new Scene(root, 600, 500));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
