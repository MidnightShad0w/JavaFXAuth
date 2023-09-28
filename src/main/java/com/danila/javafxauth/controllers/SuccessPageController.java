package com.danila.javafxauth.controllers;

import javafx.fxml.FXML;
import com.danila.javafxauth.Main;

public class SuccessPageController {

    @FXML
    private void backToLoginButtonAction() {
        Main.getInstance().switchToLoginPage();
    }
}
