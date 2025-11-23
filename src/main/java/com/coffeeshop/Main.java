package com.coffeeshop;

import com.coffeeshop.db.DBConnectMySQL;
import com.coffeeshop.handlers.LoginButtonHandler;
import com.coffeeshop.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Statement;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        DBConnectMySQL db = new DBConnectMySQL();
        Statement st = db.getStatement();

        LoginView loginView = new LoginView(st, primaryStage);
        Scene loginScene = new Scene(loginView.getLoginGUI(), 1400, 1400);

        // set from here to take the stage so we can modify later
        loginView.loginBtn.setOnAction(new LoginButtonHandler(st, loginView.usernameTextField, loginView.passwordField, primaryStage));

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("CoffeeShop POS - Login");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
