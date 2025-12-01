package com.coffeeshop.ui;

import com.coffeeshop.handlers.ExportPDFHandler;
import com.coffeeshop.handlers.GenerateReportHandler;
import com.coffeeshop.handlers.LogoutButtonHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;

public class ReportsView {

    private Statement st;
    private Stage stage;

    public ReportsView(Statement st, Stage stage) {
        this.st = st;
        this.stage = stage;
    }

    public BorderPane getReportsGUI() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");
        root.setPadding(new Insets(20));

        HBox headerPane = new HBox(10);
        headerPane.setStyle(ThemeUI.cardStyle());
        headerPane.setPadding(new Insets(12));

        Text headerTitle = new Text("Reports & Analytics");
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

        VBox leftPanel = new VBox(20);
        leftPanel.setStyle(ThemeUI.cardStyle());
        leftPanel.setPadding(new Insets(18));

        Text configTitle = new Text("Report Configuration");
        configTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        configTitle.setFont(ThemeUI.getFontBold());

        VBox periodBox = new VBox(6);
        Text periodLabel = new Text("Report Period");
        periodLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        periodLabel.setFont(ThemeUI.getFontRegular());
        periodLabel.setStyle("-fx-font-size: 14px;");

        ComboBox<String> periodComboBox = new ComboBox<>();
        periodComboBox.getItems().addAll("Today", "This Week", "This Month");
        periodComboBox.setValue("Today");
        periodComboBox.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        periodComboBox.setPrefHeight(38);
        periodComboBox.setMaxWidth(1000);
        periodBox.getChildren().addAll(periodLabel, periodComboBox);

        VBox userBox = new VBox(6);
        Text userLabel = new Text("Filter by User");
        userLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        userLabel.setFont(ThemeUI.getFontRegular());
        userLabel.setStyle("-fx-font-size: 14px;");

        ComboBox<String> usersComboBox = new ComboBox<>();
        usersComboBox.getItems().add("All Users");
        usersComboBox.setValue("All Users");
        usersComboBox.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        usersComboBox.setPrefHeight(38);
        usersComboBox.setMaxWidth(1000);

        userBox.getChildren().addAll(userLabel, usersComboBox);
        loadUsers(usersComboBox);

        // PREVIEW AREA MUST BE DECLARED BEFORE HANDLERS
        TextArea reportPreviewArea = new TextArea();
        reportPreviewArea.setEditable(false);
        reportPreviewArea.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";" + "-fx-control-inner-background: " + ThemeUI.BG_COLOR + ";" + "-fx-text-fill: " + ThemeUI.TEXT_COLOR + ";" + "-fx-border-color: " + ThemeUI.BLACK_COLOR + ";" + "-fx-font-family: '" + ThemeUI.getFontRegular().getName() + "';" + "-fx-font-size: 12px;");
        reportPreviewArea.setText("Click 'Generate Report' to view sales data and inventory status...");

        VBox buttonsBox = new VBox(12);

        Button generateBtn = ThemeUI.createButton("Generate Report");
        generateBtn.setMaxWidth(1000);
        generateBtn.setPrefHeight(42);
        generateBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        generateBtn.setOnAction(new GenerateReportHandler(st, periodComboBox, usersComboBox, reportPreviewArea));

        Button exportBtn = ThemeUI.createButton("Export to PDF");
        exportBtn.setMaxWidth(1000);
        exportBtn.setPrefHeight(42);
        exportBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        exportBtn.setOnAction(new ExportPDFHandler(st, periodComboBox, usersComboBox));

        buttonsBox.getChildren().addAll(generateBtn, exportBtn);

        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);

        leftPanel.getChildren().addAll(configTitle, periodBox, userBox, spacer1, buttonsBox);

        VBox rightPanel = new VBox(15);
        rightPanel.setStyle(ThemeUI.cardStyle());
        rightPanel.setPadding(new Insets(18));

        Text previewTitle = new Text("Report Preview");
        previewTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        previewTitle.setFont(ThemeUI.getFontBold());

        VBox.setVgrow(reportPreviewArea, Priority.ALWAYS);
        rightPanel.getChildren().addAll(previewTitle, reportPreviewArea);

        leftPanel.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.35));
        rightPanel.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.65));

        mainContent.getChildren().addAll(leftPanel, rightPanel);
        root.setCenter(mainContent);

        return root;
    }

    private void loadUsers(ComboBox<String> usersComboBox) {
        try {
            String query = "SELECT id, display_name FROM users ORDER BY id";
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("display_name");
                usersComboBox.getItems().add(id + " - " + name);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}