package com.coffeeshop.handlers;

import com.coffeeshop.ui.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginButtonHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TextField usernameTextField;
    private PasswordField passwordField;
    private Stage stage;

    public LoginButtonHandler(Statement st, TextField usernameTextField, PasswordField passwordField, Stage stage) {
        this.st = st;
        this.usernameTextField = usernameTextField;
        this.passwordField = passwordField;
        this.stage = stage;
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            String username = usernameTextField.getText();
            String password = passwordField.getText();

            if (username.isBlank() || password.isBlank()) {
                System.out.println("Empty fields");
                return;
            }

            String hashedPassword = sha256(password);
            // to be safe from sql injections:
            // if a user tries to enter: SELECT * FROM users WHERE username=''  OR 1=1 --' (1=1 always true + comments the rest)
            // by replacing ' with '': SELECT * FROM users WHERE username='  '' OR 1=1 --  '
            String safeUsername = username.replace("'", "''");

            ResultSet rs = st.executeQuery("SELECT role FROM users " +"WHERE username='" + safeUsername + "' " +"AND password_hash='" + hashedPassword + "'");

            if (rs.next()) {
                String role = rs.getString("role");

                if (role.equalsIgnoreCase("ADMIN")) {
                    TabPane tabPane = new TabPane();
                    ThemeUI.applyTabPaneTheme(tabPane);

                    OrdersView ordersView = new OrdersView();
                    Tab ordersTab = new Tab("Orders", ordersView.getOrdersGUI());
                    ordersTab.setClosable(false);

                    InventoryView inventoryView = new InventoryView();
                    Tab inventoryTab = new Tab("Inventory", inventoryView.getInventoryGUI());
                    inventoryTab.setClosable(false);

                    UsersView usersView = new UsersView();
                    Tab usersTab = new Tab("Users", usersView.getUsersGUI());
                    usersTab.setClosable(false);

                    ReportsView reportsView = new ReportsView();
                    Tab reportsTab = new Tab("Reports", reportsView.getReportsGUI());
                    reportsTab.setClosable(false);

                    tabPane.getTabs().addAll(ordersTab, inventoryTab, usersTab, reportsTab);

                    Scene dashboard = new Scene(tabPane, 1400, 1400);
                    stage.setScene(dashboard);
                    stage.setTitle("CoffeeShop POS - Admin");
                    stage.setFullScreen(true);

                } else {
                    OrdersView ordersView = new OrdersView();
                    Scene ordersScene = new Scene(ordersView.getOrdersGUI(), 1400, 1400);

                    stage.setScene(ordersScene);
                    stage.setTitle("CoffeeShop POS - Cashier");
                    stage.setFullScreen(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sha256(String t) {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            return java.util.HexFormat.of().formatHex(d.digest(t.getBytes()));
        } catch (Exception e) {
            return "";
        }
    }
}
