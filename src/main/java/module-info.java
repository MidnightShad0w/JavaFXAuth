module com.danila.javafxauth {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires zip4j;

    opens com.danila.javafxauth to javafx.fxml;
    exports com.danila.javafxauth;
    exports com.danila.javafxauth.controllers;
    opens com.danila.javafxauth.controllers to javafx.fxml;
    exports com.danila.javafxauth.model;
}