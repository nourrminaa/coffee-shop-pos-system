package com.coffeeshop.handlers;

import com.coffeeshop.factory.DefaultUserFactory;
import com.coffeeshop.factory.IUserFactory;
import com.coffeeshop.models.Admin;
import com.coffeeshop.models.Cashier;
import com.coffeeshop.models.User;
import com.coffeeshop.ui.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

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
            OrdersView view = new OrdersView();
            if (st == null) {
                view.showWarning("Error!", "Database not connected.");
                return;
            }
            String username = usernameTextField.getText();
            String password = passwordField.getText();

            // blank means empty or only spaces
            if (username.isBlank() || password.isBlank()) {
                System.err.println("Username and password cannot be blank.");
                return;
            }

            String hashedPassword = ThemeUI.sha256(password);

            // to be safe from sql injections:
            // if a user tries to enter: SELECT * FROM users WHERE username=''  OR 1=1 --' (1=1 always true + comments the rest)
            // by replacing ' with '': SELECT * FROM users WHERE username='  '' OR 1=1 --  '
            String safeUsername = username.replace("'", "''");

            ResultSet rs = st.executeQuery("SELECT id, username, password_hash, display_name, role FROM users WHERE username='" + safeUsername + "' AND password_hash='" + hashedPassword + "'");

            if (!rs.next()) {
                System.err.println("Invalid username or password");
                return;
            }

            IUserFactory userFactory = new DefaultUserFactory();

            User loggedUser = userFactory.create(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("display_name"),
                    rs.getString("role")
            );

            if (loggedUser instanceof Admin) {
                TabPane tabPane = new TabPane();
                ThemeUI.applyTabPaneTheme(tabPane); // function that styles the tabPane

                Tab ordersTab = new Tab("Orders Tab", new OrdersView(st, stage, loggedUser.getId()).getOrdersGUI());
                ordersTab.setClosable(false); // to not close by accident the tab

                Tab inventoryTab = new Tab("Inventory Management Tab", new InventoryView(st, stage).getInventoryGUI());
                inventoryTab.setClosable(false);

                Tab usersTab = new Tab("Users Management Tab", new UsersView(st, stage).getUsersGUI());
                usersTab.setClosable(false);

                Tab reportsTab = new Tab("Reports Tab", new ReportsView(st, stage).getReportsGUI());
                reportsTab.setClosable(false);
                tabPane.getTabs().addAll(ordersTab, inventoryTab, usersTab, reportsTab);

                Scene scene = new Scene(tabPane, 1400, 1400);
                stage.setScene(scene);
                stage.setTitle("CoffeeShop POS - Admin");
                stage.setFullScreen(true);

            } else if (loggedUser instanceof Cashier) {
                // we did not create a Tab/TabPane here because the cashier only has 1 pane (orders pane)
                Scene scene = new Scene(new OrdersView(st, stage, loggedUser.getId()).getOrdersGUI(), 1400, 1400);

                stage.setScene(scene);
                stage.setTitle("CoffeeShop POS - Cashier");
                stage.setFullScreen(true);
            } else {
                System.err.println("Unknown user role.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during login process.");
        }
    }
}
