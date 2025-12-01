package com.coffeeshop.handlers;

import com.coffeeshop.ui.LoginView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Statement;

public class LogoutButtonHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private Stage stage;

    public LogoutButtonHandler(Statement st, Stage stage) {
        this.st = st;
        this.stage = stage;
    }

    @Override
    public void handle(ActionEvent event) {
        LoginView loginView = new LoginView(st, stage);
        Scene loginScene = new Scene(loginView.getLoginGUI(), 1400, 800);
        loginView.loginBtn.setOnAction(new LoginButtonHandler(st, loginView.usernameTextField, loginView.passwordField, stage));

        stage.setScene(loginScene);
        stage.setTitle("CoffeeShop POS - Login");
        stage.setFullScreen(true);
    }
}