package com.coffeeshop.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginView {

    public HBox getLoginGUI() {

        // root layout: the horizontal box that holds the left & right panes
        HBox root = new HBox();
        root.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");

        // LEFT PANE (logo area)
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setStyle("-fx-background-color: " + ThemeUI.BLACK_COLOR + ";");

        // load logo
        Image logoImg = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView logo = new ImageView(logoImg); // makes an image view to display it
        logo.setFitWidth(400);
        logo.setPreserveRatio(true); // to preserve logo proportions due to setting manually a new width

        leftPane.getChildren().add(logo);

        // RIGHT PANE (login area)
        VBox rightPane = new VBox(25); // 25 is the spacing between the elements
        rightPane.setAlignment(Pos.CENTER_LEFT);
        rightPane.setPadding(new Insets(80));
        rightPane.setStyle(ThemeUI.cardStyle());

        Text loginTitle = new Text("Welcome Back!");
        loginTitle.setFont(ThemeUI.getFontBold());
        // used the Color.web to be able to use the String defined in Theme Class for modular colors
        loginTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));

        TextField username = ThemeUI.createTextField("Username");
        // used PasswordField to hide the text being written
        PasswordField password = ThemeUI.createPasswordField("Password");
        Button loginButton = ThemeUI.createButton("Login");
        loginButton.setPrefWidth(500);
        // added to keep the login button design the same during events
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(ThemeUI.buttonPrimary()));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(ThemeUI.buttonPrimary()));

        rightPane.getChildren().addAll(loginTitle, username, password, loginButton);

        // set the prefered width of both panes to half the root/display resolution of our
        // screen to always have a half left pane/ half right pane design
        leftPane.prefWidthProperty().bind(root.widthProperty().multiply(0.50));
        rightPane.prefWidthProperty().bind(root.widthProperty().multiply(0.50));

        root.getChildren().addAll(leftPane, rightPane);

        return root;
    }

//    @Override
//    public void start(Stage primaryStage) {
//
//        HBox root = getLoginGUI();
//
//        Scene scene = new Scene(root, 4000, 4000);
//
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("CoffeeShop POS - login");
//        primaryStage.setFullScreen(true); // to launch the stage in full screen
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
}
