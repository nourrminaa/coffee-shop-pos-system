package com.coffeeshop.utils;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

import java.security.MessageDigest;

public class ThemeUI {

    // colors
    public static String BG_COLOR = "#F5F2EB";
    public static String BLACK_COLOR = "#171715";
    public static String TEXT_COLOR = "#111111";
    public static String GOLD_COLOR = "#D6B37A";

    // fonts
    private static Font FONT_REGULAR = Font.loadFont(ThemeUI.class.getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 18);
    private static Font FONT_BOLD = Font.loadFont(ThemeUI.class.getResourceAsStream("/fonts/Montserrat-Bold.ttf"), 28);

    // component .setStyle String
    public static String buttonPrimary() {
        return "-fx-background-color: " + BLACK_COLOR + ";" +
                "-fx-text-fill: " + BG_COLOR + ";" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;" +
                "-fx-padding: 12 28;" +
                "-fx-background-insets: 0;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Montserrat';"+
                "-fx-font-size: 18px;" +
                "-fx-cursor: hand;";
    }

    public static String textFieldStyle() {
        return "-fx-background-color: "+ BG_COLOR+";" +
                "-fx-border-color: " + BLACK_COLOR + ";" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;" +
                "-fx-text-fill: " + TEXT_COLOR + ";" +
                "-fx-font-family: 'Montserrat';" +
                "-fx-padding: 10 14;";
    }


    public static String cardStyle() {
        return "-fx-background-color: " + BG_COLOR + ";" +
                "-fx-border-color: " + BLACK_COLOR + ";" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;";
    }

    public static String tabStyle() {
        return "-fx-background-color: " + BLACK_COLOR + ";" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;" +
                "-fx-cursor: hand;";
    }

    public static String tabLabelStyle() {
        return "-fx-text-fill: " + BG_COLOR + ";" +
                "-fx-font-family: 'Montserrat Bold';" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;";
    }

    // components factories


    public static void applyTabPaneTheme(TabPane tabPane) {
        // basically a Tab is not a Node by default like the rest of the ui components
        // and in order to manipulate a ui using css it NEEDS to be a Node
        // the visible tab button we click on is NOT the Tab:
        // it is a StackPane created later by the TabPaneSkin -> converts the Tab into a Node
        // so JavaFX internally:
        // for each Tab:
        // create a StackPane with styleclass "tab"
        // inside it create a Label with styleclass "tab-label" (we can use the .lookupAll method to gather all teh classes related)
        // so, in order to change the css of a Tab we need to wait until runtime, after UI is shown
        tabPane.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            // adding an event listener obs that observes changes in the skin property: "when the skin changes (from null to real skin), run the code"
            if (newSkin != null) {
                tabPane.lookupAll(".tab-header-background").forEach(node ->
                        node.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";")
                );
                tabPane.lookupAll(".tab").forEach(tabNode ->
                        tabNode.setStyle(ThemeUI.tabStyle())
                );
                tabPane.lookupAll(".tab-label").forEach(label ->
                        label.setStyle(ThemeUI.tabLabelStyle())
                );
            }
        });
    }

    public static Button createButton(String text) {
        Button btn = new Button(text);
        btn.setFont(FONT_REGULAR);
        btn.setStyle(buttonPrimary());
        btn.setPrefHeight(45);
        return btn;
    }

    public static TextField createTextField(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setFont(FONT_REGULAR);
        tf.setStyle(textFieldStyle());
        tf.setPrefHeight(45);
        return tf;
    }

    public static PasswordField createPasswordField(String placeholder) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(placeholder);
        pf.setFont(FONT_REGULAR);
        pf.setStyle(textFieldStyle() + "-fx-font-family: 'Montserrat';");
        pf.setPrefHeight(45);
        return pf;
    }

    public static Font getFontRegular() { return FONT_REGULAR; }
    public static Font getFontBold() { return FONT_BOLD; }

    public static String tableStyle() {
        return "-fx-background-color: " + BG_COLOR + ";" +
                "-fx-control-inner-background: " + BG_COLOR + ";" +
                "-fx-table-cell-border-color: " + BLACK_COLOR + ";" +
                "-fx-border-color: " + BLACK_COLOR + ";";
    }

    public static String sha256(String t) {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            return java.util.HexFormat.of().formatHex(d.digest(t.getBytes()));
        } catch (Exception e) {
            System.out.println("Error in generating hash.");
            return "";
        }
    }

    public static String iconButtonStyle() {
        return "-fx-background-color: " + BLACK_COLOR + ";" +
                "-fx-background-radius: 50;" +
                "-fx-border-radius: 50;" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;";
    }

    public static Button createIconButton(String fileName) {
        Image img = new Image(ThemeUI.class.getResourceAsStream("/icons/" + fileName));

        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.setFitWidth(50);

        Button b = new Button();
        b.setGraphic(iv);
        b.setStyle(iconButtonStyle());

        return b;
    }
}