package com.danila.javafxauth.controllers;

import com.danila.javafxauth.Main;
import javafx.fxml.FXML;

public class AccessDeniedPageController {

    @FXML
    private void backToLoginButtonAction() {
        Main.getInstance().switchToLoginPage();
    }
}
