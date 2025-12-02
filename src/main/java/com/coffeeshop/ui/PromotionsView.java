package com.coffeeshop.ui;

import com.coffeeshop.handlers.AddPromotionHandler;
import com.coffeeshop.handlers.UpdatePromotionHandler;
import com.coffeeshop.handlers.DeletePromotionHandler;
import com.coffeeshop.models.PromotionRow;
import com.coffeeshop.utils.ThemeUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;

public class PromotionsView {

    private Statement st;
    private Stage stage;
    private OrdersView ordersView;

    private TableView<PromotionRow> promotionsTable;
    private TextField nameField;
    private TextField percentField;
    private CheckBox activeCheckBox;
    private Text formError;

    public PromotionsView(Statement st, Stage stage, OrdersView ordersView) {
        this.st = st;
        this.stage = stage;
        this.ordersView = ordersView;
    }

    public BorderPane getPromotionsGUI() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");
        root.setPadding(new Insets(20));

        HBox headerPane = new HBox(10);
        headerPane.setStyle(ThemeUI.cardStyle());
        headerPane.setPadding(new Insets(12));
        headerPane.setAlignment(Pos.CENTER_LEFT);

        Text headerTitle = new Text("Promotions Management");
        headerTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        headerTitle.setFont(ThemeUI.getFontBold());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = ThemeUI.createButton("Logout");
        logoutBtn.setPrefHeight(40);
        logoutBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        logoutBtn.setOnAction(new com.coffeeshop.handlers.LogoutButtonHandler(st, stage));

        headerPane.getChildren().addAll(headerTitle, spacer, logoutBtn);

        root.setTop(headerPane);

        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20, 0, 0, 0));

        VBox leftPane = new VBox(15);
        leftPane.setStyle(ThemeUI.cardStyle());
        leftPane.setPadding(new Insets(18));

        Text promotionsListTitle = new Text("Promotions List");
        promotionsListTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        promotionsListTitle.setFont(ThemeUI.getFontBold());

        promotionsTable = new TableView<>();
        promotionsTable.setPrefHeight(1000);
        promotionsTable.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";" + "-fx-control-inner-background: " + ThemeUI.BG_COLOR + ";" + "-fx-text-fill: " + ThemeUI.TEXT_COLOR + ";" + "-fx-border-color: " + ThemeUI.BLACK_COLOR + ";");
        promotionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PromotionRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-font-size: 14px;");

        TableColumn<PromotionRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-font-size: 14px;");

        TableColumn<PromotionRow, Integer> percentCol = new TableColumn<>("Percent Off");
        percentCol.setCellValueFactory(new PropertyValueFactory<>("percentOff"));
        percentCol.setStyle("-fx-font-size: 14px;");

        TableColumn<PromotionRow, String> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setStyle("-fx-font-size: 14px;");

        promotionsTable.getColumns().addAll(idCol, nameCol, percentCol, activeCol);

        promotionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow == null) return;
            nameField.setText(newRow.getName());
            percentField.setText(String.valueOf(newRow.getPercentOff()));
            activeCheckBox.setSelected("Yes".equalsIgnoreCase(newRow.getActive()));
        });

        leftPane.getChildren().addAll(promotionsListTitle, promotionsTable);

        VBox rightPane = new VBox(15);
        rightPane.setStyle(ThemeUI.cardStyle());
        rightPane.setPadding(new Insets(18));

        Text formTitle = new Text("Add / Edit Promotion");
        formTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        formTitle.setFont(ThemeUI.getFontBold());

        formError = new Text(" ");
        formError.setFill(Color.RED);
        formError.setStyle("-fx-font-size: 13px;");

        VBox nameBox = new VBox(6);
        Text nameLabel = new Text("Promotion Name");
        nameLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        nameLabel.setFont(ThemeUI.getFontRegular());
        nameLabel.setStyle("-fx-font-size: 14px;");
        nameField = ThemeUI.createTextField("Enter promotion name");
        nameField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        nameField.setPrefHeight(38);
        nameBox.getChildren().addAll(nameLabel, nameField);

        VBox percentBox = new VBox(6);
        Text percentLabel = new Text("Percent Off");
        percentLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        percentLabel.setFont(ThemeUI.getFontRegular());
        percentLabel.setStyle("-fx-font-size: 14px;");
        percentField = ThemeUI.createTextField("e.g. 20");
        percentField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        percentField.setPrefHeight(38);
        percentBox.getChildren().addAll(percentLabel, percentField);

        HBox flagsBox = new HBox(20);
        flagsBox.setAlignment(Pos.CENTER_LEFT);
        activeCheckBox = new CheckBox("Active");
        activeCheckBox.setSelected(true);
        activeCheckBox.setTextFill(Color.web(ThemeUI.TEXT_COLOR));
        activeCheckBox.setStyle("-fx-font-size: 14px;");
        flagsBox.getChildren().addAll(activeCheckBox);

        Button addPromotionBtn = ThemeUI.createButton("Add Promotion");
        addPromotionBtn.setMaxWidth(1400);
        addPromotionBtn.setPrefHeight(42);
        addPromotionBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        addPromotionBtn.setOnAction(new AddPromotionHandler(st, nameField, percentField, activeCheckBox, this, ordersView));

        Button updatePromotionBtn = ThemeUI.createButton("Update Promotion");
        updatePromotionBtn.setMaxWidth(1400);
        updatePromotionBtn.setPrefHeight(42);
        updatePromotionBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        updatePromotionBtn.setOnAction(new UpdatePromotionHandler(st, promotionsTable, nameField, percentField, activeCheckBox, this, ordersView));

        Button deletePromotionBtn = ThemeUI.createButton("Delete Promotion");
        deletePromotionBtn.setMaxWidth(1400);
        deletePromotionBtn.setPrefHeight(42);
        deletePromotionBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        deletePromotionBtn.setOnAction(new DeletePromotionHandler(st, promotionsTable, this, ordersView));

        rightPane.getChildren().addAll(formTitle, formError, nameBox, percentBox, flagsBox, addPromotionBtn, updatePromotionBtn, deletePromotionBtn);

        leftPane.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.75));
        rightPane.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.25));

        mainContent.getChildren().addAll(leftPane, rightPane);
        root.setCenter(mainContent);

        loadPromotions();

        return root;
    }

    public void loadPromotions() {
        promotionsTable.getItems().clear();
        try {
            String query = "SELECT id, name, percent_off, is_active FROM promotions ORDER BY name";
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int percent = rs.getInt("percent_off");
                boolean isActive = rs.getBoolean("is_active");
                String active = isActive ? "Yes" : "No";
                promotionsTable.getItems().add(new PromotionRow(id, name, percent, active));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearForm() {
        nameField.clear();
        percentField.clear();
        activeCheckBox.setSelected(true);
    }

    public void setFormError(String message) {
        formError.setText(message);
    }

    public boolean validateInputs() {
        String name = nameField.getText().trim();
        String percentText = percentField.getText().trim();
        if (name.isEmpty() || percentText.isEmpty()) {
            formError.setText("Invalid input! Please check all fields.");
            return false;
        }
        try {
            Integer.parseInt(percentText);
        } catch (NumberFormatException e) {
            formError.setText("Percent must be a number.");
            return false;
        }
        formError.setText(" ");
        return true;
    }

    public TableView<PromotionRow> getPromotionsTable() {
        return promotionsTable;
    }
}