package com.coffeeshop.ui;

import com.coffeeshop.handlers.LogoutButtonHandler;
import com.coffeeshop.handlers.UsersHandler;
import com.coffeeshop.models.UserRow;
import com.coffeeshop.utils.ThemeUI;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Statement;

public class UsersView {

    private Statement st;
    public TableView<UserRow> usersTable;
    public TextField usernameField;
    public PasswordField passwordField;
    public TextField displayNameField;
    public ComboBox<String> roleComboBox;
    private Stage stage;

    public UsersView(Statement st, Stage stage) {
        this.st = st;
        this.stage = stage;
    }

    public BorderPane getUsersGUI() {
        UsersHandler handler = new UsersHandler(st, this);
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");
        root.setPadding(new Insets(20));

        HBox headerPane = new HBox(10);
        headerPane.setStyle(ThemeUI.cardStyle());
        headerPane.setPadding(new Insets(12));

        Text headerTitle = new Text("User Management");
        headerTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        headerTitle.setFont(ThemeUI.getFontBold());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = ThemeUI.createButton("Logout");
        logoutBtn.setPrefHeight(40);
        logoutBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        logoutBtn.setOnAction(new LogoutButtonHandler(st, stage));

        headerPane.getChildren().addAll(headerTitle, spacer, logoutBtn);
        root.setTop(headerPane);

        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20, 0, 0, 0));

        VBox leftPane = new VBox(15);
        leftPane.setStyle(ThemeUI.cardStyle());
        leftPane.setPadding(new Insets(18));

        Text usersListTitle = new Text("Users List");
        usersListTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        usersListTitle.setFont(ThemeUI.getFontBold());

        usersTable = new TableView<>();
        usersTable.setStyle(ThemeUI.tableStyle());
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // used to resize columns equally

        TableColumn<UserRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-font-size: 14px;");

        TableColumn<UserRow, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setStyle("-fx-font-size: 14px;");

        TableColumn<UserRow, String> displayNameCol = new TableColumn<>("Display Name");
        displayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        displayNameCol.setStyle("-fx-font-size: 14px;");

        TableColumn<UserRow, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setStyle("-fx-font-size: 14px;");

        TableColumn<UserRow, String> createdCol = new TableColumn<>("Created At");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdCol.setStyle("-fx-font-size: 14px;");

        usersTable.getColumns().addAll(idCol, usernameCol, displayNameCol, roleCol, createdCol);
        usersTable.setPrefHeight(1000);

        leftPane.getChildren().addAll(usersListTitle, usersTable);

        VBox rightPane = new VBox(15);
        rightPane.setStyle(ThemeUI.cardStyle());
        rightPane.setPadding(new Insets(18));

        Text formTitle = new Text("Add New User");
        formTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        formTitle.setFont(ThemeUI.getFontBold());

        VBox usernameBox = new VBox(6);
        Text usernameLabel = new Text("Username");
        usernameLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        usernameLabel.setFont(ThemeUI.getFontRegular());
        usernameLabel.setStyle("-fx-font-size: 14px;");

        usernameField = ThemeUI.createTextField("Enter username");
        usernameField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        usernameField.setPrefHeight(38);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        VBox passwordBox = new VBox(6);
        Text passwordLabel = new Text("Password");
        passwordLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        passwordLabel.setFont(ThemeUI.getFontRegular());
        passwordLabel.setStyle("-fx-font-size: 14px;");

        passwordField = ThemeUI.createPasswordField("Enter password");
        passwordField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        passwordField.setFont(ThemeUI.getFontRegular());
        passwordField.setPrefHeight(38);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        VBox displayNameBox = new VBox(6);
        Text displayNameLabel = new Text("Display Name");
        displayNameLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        displayNameLabel.setFont(ThemeUI.getFontRegular());
        displayNameLabel.setStyle("-fx-font-size: 14px;");

        displayNameField = ThemeUI.createTextField("Enter display name");
        displayNameField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        displayNameField.setPrefHeight(38);
        displayNameBox.getChildren().addAll(displayNameLabel, displayNameField);

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
        roleBox.getChildren().addAll(roleLabel, roleComboBox);

        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);

        Button addUserBtn = ThemeUI.createButton("Add User");
        addUserBtn.setMaxWidth(1400);
        addUserBtn.setPrefHeight(42);
        addUserBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        addUserBtn.setOnAction(handler);

        Button deleteUserBtn = ThemeUI.createButton("Delete User");
        deleteUserBtn.setMaxWidth(1400);
        deleteUserBtn.setPrefHeight(42);
        deleteUserBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        deleteUserBtn.setOnAction(handler);

        rightPane.getChildren().addAll(formTitle,usernameBox,passwordBox,displayNameBox, roleBox, spacer1, addUserBtn, deleteUserBtn);

        leftPane.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.75));
        rightPane.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.25));

        mainContent.getChildren().addAll(leftPane, rightPane);
        root.setCenter(mainContent);

        handler.loadUsers();
        return root;
    }
}
