package com.coffeeshop.handlers;

import com.coffeeshop.ui.UserRow;
import com.coffeeshop.ui.UsersView;
import com.coffeeshop.ui.ThemeUI;
import com.coffeeshop.models.User;
import com.coffeeshop.factory.DefaultUserFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import java.sql.ResultSet;
import java.sql.Statement;

public class UsersHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private UsersView view;
    private DefaultUserFactory userFactory = new DefaultUserFactory();

    public UsersHandler(Statement st, UsersView view) {
        this.st = st;
        this.view = view;
    }

    @Override
    public void handle(ActionEvent e) {

        Button btn = (Button) e.getSource();

        if (btn.getText().equals("Add User")) {
            handleAddUser();
        } else if (btn.getText().equals("Delete User")) {
            handleDeleteUser();
        }
    }

    public void loadUsers() {
        view.usersTable.getItems().clear(); // to clear the table in order to refresh
        try {
            ResultSet rs = st.executeQuery("SELECT id, username, display_name, role, created_at FROM users ORDER BY id");
            while (rs.next()) {
                view.usersTable.getItems().add(new UserRow(rs.getInt("id"), rs.getString("username"), rs.getString("display_name"), rs.getString("role"), rs.getString("created_at")));
            }
            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleAddUser() {
        String username = view.usernameField.getText();
        String password = view.passwordField.getText();
        String displayName = view.displayNameField.getText();
        String role = view.roleComboBox.getValue();
        if (username.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
            System.err.println("Missing fields.");
            return;
        }
        try {
            String passHash = ThemeUI.sha256(password);
            String insertSQL = "INSERT INTO users (username, password_hash, display_name, role) VALUES (" + "'" + username + "', " + "'" + passHash + "', " + "'" + displayName + "', " + "'" + role + "')";
            st.executeUpdate(insertSQL);

            view.usernameField.clear();
            view.passwordField.clear();
            view.displayNameField.clear();
            view.roleComboBox.setValue("CASHIER");
            loadUsers();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleDeleteUser() {
        UserRow selected = view.usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.err.println("Select a user first.");
            return;
        }
        try {
            st.executeUpdate("DELETE FROM users WHERE id = " + selected.getId());
            loadUsers();
            view.usernameField.clear();
            view.passwordField.clear();
            view.displayNameField.clear();
            view.roleComboBox.setValue("CASHIER");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}