package com.coffeeshop.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.Statement;

public class UsersView {

    private Statement st;
    private TableView<UserRow> usersTable;

    // form fields
    private TextField usernameField;
    private PasswordField passwordFieldInput;
    private TextField displayNameField;
    private ComboBox<String> roleComboBox;

    public UsersView(Statement st) {
        this.st = st;
    }

    public BorderPane getUsersGUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");
        root.setPadding(new Insets(20));

        // 1. Header
        HBox headerPane = new HBox(10);
        headerPane.setStyle(ThemeUI.cardStyle());
        headerPane.setPadding(new Insets(12));
        headerPane.setAlignment(Pos.CENTER_LEFT);

        Text headerTitle = new Text("User Management");
        headerTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        headerTitle.setFont(ThemeUI.getFontBold());

        headerPane.getChildren().add(headerTitle);
        root.setTop(headerPane);

        // 2. Main content (left + right pane)
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20, 0, 0, 0));

        // 2.1. Left pane - Users table (3/4 width)
        VBox leftPane = new VBox(15);
        leftPane.setStyle(ThemeUI.cardStyle());
        leftPane.setPadding(new Insets(18));

        Text usersListTitle = new Text("Users List");
        usersListTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        usersListTitle.setFont(ThemeUI.getFontBold());

        // Create TableView
        usersTable = new TableView<>();
        usersTable.setStyle(
                "-fx-background-color: " + ThemeUI.BG_COLOR + ";" +
                        "-fx-control-inner-background: " + ThemeUI.BG_COLOR + ";" +
                        "-fx-table-cell-border-color: " + ThemeUI.BLACK_COLOR + ";" +
                        "-fx-border-color: " + ThemeUI.BLACK_COLOR + ";"
        );

        // IMPORTANT FIX → remove empty column
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ID Column
        TableColumn<UserRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        idCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");

        // Username Column
        TableColumn<UserRow, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);
        usernameCol.setStyle("-fx-font-size: 14px;");

        // Display Name Column
        TableColumn<UserRow, String> displayNameCol = new TableColumn<>("Display Name");
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        displayNameCol.setPrefWidth(200);
        displayNameCol.setStyle("-fx-font-size: 14px;");

        // Role Column
        TableColumn<UserRow, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(100);
        roleCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");

        // Created At Column
        TableColumn<UserRow, String> createdCol = new TableColumn<>("Created At");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdCol.setPrefWidth(180);
        createdCol.setStyle("-fx-font-size: 14px;");

        // Allow all columns to expand — prevents filler column
        idCol.setMaxWidth(1f * Integer.MAX_VALUE);
        usernameCol.setMaxWidth(1f * Integer.MAX_VALUE);
        displayNameCol.setMaxWidth(1f * Integer.MAX_VALUE);
        roleCol.setMaxWidth(1f * Integer.MAX_VALUE);
        createdCol.setMaxWidth(1f * Integer.MAX_VALUE);

        usersTable.getColumns().addAll(idCol, usernameCol, displayNameCol, roleCol, createdCol);
        VBox.setVgrow(usersTable, Priority.ALWAYS);

        leftPane.getChildren().addAll(usersListTitle, usersTable);

        // 2.2. Right pane - Add user form (1/4 width)
        VBox rightPane = new VBox(15);
        rightPane.setStyle(ThemeUI.cardStyle());
        rightPane.setPadding(new Insets(18));

        Text formTitle = new Text("Add New User");
        formTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        formTitle.setFont(ThemeUI.getFontBold());

        // Username field
        VBox usernameBox = new VBox(6);
        Text usernameLabel = new Text("Username");
        usernameLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        usernameLabel.setFont(ThemeUI.getFontRegular());
        usernameLabel.setStyle("-fx-font-size: 14px;");

        usernameField = ThemeUI.createTextField("Enter username");
        usernameField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        usernameField.setPrefHeight(38);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field
        VBox passwordBox = new VBox(6);
        Text passwordLabel = new Text("Password");
        passwordLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        passwordLabel.setFont(ThemeUI.getFontRegular());
        passwordLabel.setStyle("-fx-font-size: 14px;");

        passwordFieldInput = ThemeUI.createPasswordField("Enter password");
        passwordFieldInput.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        passwordFieldInput.setPrefHeight(38);
        passwordBox.getChildren().addAll(passwordLabel, passwordFieldInput);

        // Display name field
        VBox displayNameBox = new VBox(6);
        Text displayNameLabel = new Text("Display Name");
        displayNameLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        displayNameLabel.setFont(ThemeUI.getFontRegular());
        displayNameLabel.setStyle("-fx-font-size: 14px;");

        displayNameField = ThemeUI.createTextField("Enter display name");
        displayNameField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        displayNameField.setPrefHeight(38);
        displayNameBox.getChildren().addAll(displayNameLabel, displayNameField);

        // Role dropdown
        VBox roleBox = new VBox(6);
        Text roleLabel = new Text("Role");
        roleLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        roleLabel.setFont(ThemeUI.getFontRegular());
        roleLabel.setStyle("-fx-font-size: 14px;");

        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("ADMIN", "CASHIER");
        roleComboBox.setValue("CASHIER");
        roleComboBox.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        roleComboBox.setPrefHeight(38);
        roleComboBox.setMaxWidth(Double.MAX_VALUE);

        roleBox.getChildren().addAll(roleLabel, roleComboBox);

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Add user button
        Button addUserBtn = ThemeUI.createButton("Add User");
        addUserBtn.setMaxWidth(Double.MAX_VALUE);
        addUserBtn.setPrefHeight(42);
        addUserBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        addUserBtn.setOnAction(e -> handleAddUser());

        // Delete user button
        Button deleteUserBtn = ThemeUI.createButton("Delete User");
        deleteUserBtn.setMaxWidth(Double.MAX_VALUE);
        deleteUserBtn.setPrefHeight(42);
        deleteUserBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        deleteUserBtn.setOnAction(e -> handleDeleteUser());

        rightPane.getChildren().addAll(
                formTitle,
                usernameBox,
                passwordBox,
                displayNameBox,
                roleBox,
                spacer,
                addUserBtn,
                deleteUserBtn
        );

        // Set widths
        leftPane.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.75));
        rightPane.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.25));

        mainContent.getChildren().addAll(leftPane, rightPane);
        root.setCenter(mainContent);

        loadUsers();
        return root;
    }

    private void loadUsers() {
        usersTable.getItems().clear();

        try {
            String query = "SELECT id, username, display_name, role, created_at FROM users ORDER BY id";
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                usersTable.getItems().add(new UserRow(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("display_name"),
                        rs.getString("role"),
                        rs.getString("created_at")
                ));
            }

            rs.close();

        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAddUser() {
        String username = usernameField.getText().trim();
        String password = passwordFieldInput.getText();
        String displayName = displayNameField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
            showError("All fields are required!");
            return;
        }

        try {
            String passwordHash = ThemeUI.sha256(password);

            String insertQuery = String.format(
                    "INSERT INTO users (username, password_hash, display_name, role) VALUES ('%s', '%s', '%s', '%s')",
                    escapeString(username),
                    passwordHash,
                    escapeString(displayName),
                    role
            );

            int rowsAffected = st.executeUpdate(insertQuery);

            if (rowsAffected > 0) {
                usernameField.clear();
                passwordFieldInput.clear();
                displayNameField.clear();
                roleComboBox.setValue("CASHIER");

                loadUsers();
            }

        } catch (Exception e) {
            showError("Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteUser() {
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select a user to delete.");
            return;
        }

        try {
            int rows = st.executeUpdate("DELETE FROM users WHERE id = " + selected.getId());

            if (rows > 0) {
                loadUsers();

                usernameField.clear();
                passwordFieldInput.clear();
                displayNameField.clear();
                roleComboBox.setValue("CASHIER");
            }

        } catch (Exception e) {
            showError("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String escapeString(String input) { return input.replace("'", "''"); }

    private void showError(String message) { System.err.println("ERROR: " + message); }

    // Inner class for TableView rows
    public static class UserRow {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty username;
        private final SimpleStringProperty displayName;
        private final SimpleStringProperty role;
        private final SimpleStringProperty createdAt;

        public UserRow(int id, String username, String displayName, String role, String createdAt) {
            this.id = new SimpleIntegerProperty(id);
            this.username = new SimpleStringProperty(username);
            this.displayName = new SimpleStringProperty(displayName);
            this.role = new SimpleStringProperty(role);
            this.createdAt = new SimpleStringProperty(createdAt);
        }

        public int getId() { return id.get(); }
        public String getUsername() { return username.get(); }
        public String getDisplayName() { return displayName.get(); }
        public String getRole() { return role.get(); }
        public String getCreatedAt() { return createdAt.get(); }
    }
}
