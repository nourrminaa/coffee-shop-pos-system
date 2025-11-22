package com.coffeeshop.ui;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class ThemeUI {

    // colors
    public static final String BG_COLOR = "#DFD8C9";
    public static final String BLACK_COLOR = "#171715";
    public static final String TEXT_COLOR = "#111111";
    public static final String GOLD_COLOR = "#D6B37A";

    // fonts
    private static final Font FONT_REGULAR = Font.loadFont(ThemeUI.class.getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 18);

    private static final Font FONT_BOLD = Font.loadFont(ThemeUI.class.getResourceAsStream("/fonts/Montserrat-Bold.ttf"), 28);

    // component .setStyle String
    public static String buttonPrimary() {
        return "-fx-background-color: " + BLACK_COLOR + ";" +
                "-fx-text-fill: " + BG_COLOR + ";" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;" +
                "-fx-padding: 12 28;" +
                "-fx-background-insets: 0;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Montserrat Bold';" +
                "-fx-font-size: 18px;" +
                "-fx-cursor: hand;";
    }

    public static String textFieldStyle() {
        return "-fx-background-color: "+ BG_COLOR+";" +
                "-fx-border-color: " + BLACK_COLOR + ";" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;" +
                "-fx-text-fill: " + TEXT_COLOR + ";" +
                "-fx-padding: 10 14;";
    }

    public static String cardStyle() {
        return "-fx-background-color: " + BG_COLOR + ";" +
                "-fx-border-color: " + BLACK_COLOR + ";" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;";
    }

    // components factories
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
        pf.setStyle(textFieldStyle());
        pf.setPrefHeight(45);
        return pf;
    }

    public static Font getFontRegular() { return FONT_REGULAR; }
    public static Font getFontBold() { return FONT_BOLD; }
}
