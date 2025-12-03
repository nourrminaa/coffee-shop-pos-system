package com.coffeeshop.ui;

import com.coffeeshop.handlers.AddProductHandler;
import com.coffeeshop.handlers.LogoutButtonHandler;
import com.coffeeshop.handlers.UpdateProductHandler;
import com.coffeeshop.handlers.DeleteProductHandler;
import com.coffeeshop.models.ProductRow;
import com.coffeeshop.utils.ThemeUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;

public class InventoryView {

    private Statement st;
    private Stage stage;

    // added here to make them accessible to the helper functions
    private TextField nameField;
    private TextField categoryField;
    private TextField priceField;
    private TextField stockField;
    private TextField minStockField;
    private CheckBox addonCheckBox;
    private CheckBox activeCheckBox;
    private TableView<ProductRow> productsTable;
    private Text formError;

    // added to be able to refresh the orders view when admin adds/deletes/updates a new item
    private OrdersView ordersView;

    public InventoryView(Statement st, Stage stage, OrdersView ordersView) {
        this.st = st;
        this.stage = stage;
        this.ordersView = ordersView;
    }

    public BorderPane getInventoryGUI() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");
        root.setPadding(new Insets(20));

        HBox headerPane = new HBox(10);
        headerPane.setStyle(ThemeUI.cardStyle());
        headerPane.setPadding(new Insets(12));
        headerPane.setAlignment(Pos.CENTER_LEFT);

        Text headerTitle = new Text("Inventory Management");
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

        // main
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20, 0, 0, 0));

        // left side - inventory products table
        VBox leftPane = new VBox(15);
        leftPane.setStyle(ThemeUI.cardStyle());
        leftPane.setPadding(new Insets(18));

        Text productsListTitle = new Text("Products List");
        productsListTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        productsListTitle.setFont(ThemeUI.getFontBold());

        productsTable = new TableView<>();
        productsTable.setPrefHeight(1000);

        productsTable.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";" + "-fx-control-inner-background: " + ThemeUI.BG_COLOR + ";" + "-fx-text-fill: " + ThemeUI.TEXT_COLOR + ";" + "-fx-border-color: " + ThemeUI.BLACK_COLOR + ";");
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ProductRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-font-size: 14px;");

        TableColumn<ProductRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-font-size: 14px;");

        TableColumn<ProductRow, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setStyle("-fx-font-size: 14px;");

        TableColumn<ProductRow, Integer> priceCol = new TableColumn<>("Price (LBP)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("priceLbp"));
        priceCol.setStyle("-fx-font-size: 14px;");

        TableColumn<ProductRow, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQty"));
        stockCol.setStyle("-fx-font-size: 14px;");

        TableColumn<ProductRow, Integer> minStockCol = new TableColumn<>("Min Stock");
        minStockCol.setCellValueFactory(new PropertyValueFactory<>("minStockQty"));
        minStockCol.setStyle("-fx-font-size: 14px;");

        TableColumn<ProductRow, String> addonCol = new TableColumn<>("Add-on");
        addonCol.setCellValueFactory(new PropertyValueFactory<>("addon"));
        addonCol.setStyle("-fx-font-size: 14px;");

        TableColumn<ProductRow, String> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setStyle("-fx-font-size: 14px;");

        productsTable.getColumns().addAll(idCol, nameCol, categoryCol, priceCol, stockCol, minStockCol, addonCol, activeCol);

        // when selecting a row, add its details in the form on the right
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow == null) return;
            nameField.setText(newRow.getName());
            categoryField.setText(newRow.getCategory());
            priceField.setText(String.valueOf(newRow.getPriceLbp()));
            stockField.setText(String.valueOf(newRow.getStockQty()));
            minStockField.setText(String.valueOf(newRow.getMinStockQty()));
            addonCheckBox.setSelected("Yes".equalsIgnoreCase(newRow.getAddon()));
            activeCheckBox.setSelected("Yes".equalsIgnoreCase(newRow.getActive()));
        });

        leftPane.getChildren().addAll(productsListTitle, productsTable);

        // right side - form
        VBox rightPane = new VBox(15);
        rightPane.setStyle(ThemeUI.cardStyle());
        rightPane.setPadding(new Insets(18));

        Text formTitle = new Text("Add / Edit Product");
        formTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        formTitle.setFont(ThemeUI.getFontBold());

        formError = new Text(" ");
        formError.setFill(Color.RED);
        formError.setStyle("-fx-font-size: 13px;");

        // name
        VBox nameBox = new VBox(6);
        Text nameLabel = new Text("Product Name");
        nameLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        nameLabel.setFont(ThemeUI.getFontRegular());
        nameLabel.setStyle("-fx-font-size: 14px;");

        nameField = ThemeUI.createTextField("Enter product name");
        nameField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        nameField.setPrefHeight(38);
        nameBox.getChildren().addAll(nameLabel, nameField);

        // category
        VBox categoryBox = new VBox(6);
        Text categoryLabel = new Text("Category");
        categoryLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        categoryLabel.setFont(ThemeUI.getFontRegular());
        categoryLabel.setStyle("-fx-font-size: 14px;");

        categoryField = ThemeUI.createTextField("Coffee, Tea, Pastries...");
        categoryField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        categoryField.setPrefHeight(38);
        categoryBox.getChildren().addAll(categoryLabel, categoryField);

        // price
        VBox priceBox = new VBox(6);
        Text priceLabel = new Text("Price (LBP)");
        priceLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        priceLabel.setFont(ThemeUI.getFontRegular());
        priceLabel.setStyle("-fx-font-size: 14px;");

        priceField = ThemeUI.createTextField("e.g. 70000");
        priceField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        priceField.setPrefHeight(38);
        priceBox.getChildren().addAll(priceLabel, priceField);

        // stock quantity
        VBox stockBox = new VBox(6);
        Text stockLabel = new Text("Stock Quantity");
        stockLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        stockLabel.setFont(ThemeUI.getFontRegular());
        stockLabel.setStyle("-fx-font-size: 14px;");

        stockField = ThemeUI.createTextField("e.g. 20");
        stockField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        stockField.setPrefHeight(38);
        stockBox.getChildren().addAll(stockLabel, stockField);

        // min stock
        VBox minStockBox = new VBox(6);
        Text minStockLabel = new Text("Minimum Stock");
        minStockLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        minStockLabel.setFont(ThemeUI.getFontRegular());
        minStockLabel.setStyle("-fx-font-size: 14px;");

        minStockField = ThemeUI.createTextField("e.g. 5");
        minStockField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        minStockField.setPrefHeight(38);
        minStockBox.getChildren().addAll(minStockLabel, minStockField);

        // row checkboxes
        HBox flagsBox = new HBox(20);
        flagsBox.setAlignment(Pos.CENTER_LEFT);

        addonCheckBox = new CheckBox("Add-on item");
        addonCheckBox.setTextFill(Color.web(ThemeUI.TEXT_COLOR));
        addonCheckBox.setStyle("-fx-font-size: 14px;");

        activeCheckBox = new CheckBox("Active");
        activeCheckBox.setSelected(true);
        activeCheckBox.setTextFill(Color.web(ThemeUI.TEXT_COLOR));
        activeCheckBox.setStyle("-fx-font-size: 14px;");

        flagsBox.getChildren().addAll(addonCheckBox, activeCheckBox);

        // buttons
        Button addProductBtn = ThemeUI.createButton("Add Product");
        addProductBtn.setMaxWidth(1400);
        addProductBtn.setPrefHeight(42);
        addProductBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        addProductBtn.setOnAction(new AddProductHandler(st, nameField, categoryField, priceField, stockField, minStockField, addonCheckBox, activeCheckBox
                , this // pass this class (Inventory View) to access the refresh methods
                , ordersView)); // to update the categories & products
        Button updateProductBtn = ThemeUI.createButton("Update Product");
        updateProductBtn.setMaxWidth(1400);
        updateProductBtn.setPrefHeight(42);
        updateProductBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        updateProductBtn.setOnAction(new UpdateProductHandler(st, productsTable, nameField, categoryField, priceField, stockField, minStockField, addonCheckBox, activeCheckBox,
                this
                , ordersView));
        Button deleteProductBtn = ThemeUI.createButton("Delete Product");
        deleteProductBtn.setMaxWidth(1400);
        deleteProductBtn.setPrefHeight(42);
        deleteProductBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        deleteProductBtn.setOnAction(new DeleteProductHandler(st, productsTable,
                this
                , ordersView));
        rightPane.getChildren().addAll(formTitle,formError, nameBox, categoryBox, priceBox, stockBox, minStockBox, flagsBox, addProductBtn, updateProductBtn, deleteProductBtn);

        // width ratio
        leftPane.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.75));
        rightPane.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.25));

        ScrollPane rightScroll = new ScrollPane(rightPane);
        rightScroll.setFitToWidth(true);
        rightScroll.setStyle("-fx-background: transparent; -fx-control-inner-background: " + ThemeUI.BG_COLOR + ";");
        rightScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScroll.setPrefWidth(380);

        mainContent.getChildren().addAll(leftPane, rightScroll);
        root.setCenter(mainContent);
        loadProducts();

        return root;
    }

    public void loadProducts() {
        productsTable.getItems().clear();
        try {
            String query = "SELECT id, name, category, price_lbp, stock_qty, min_stock_qty, is_addon, is_active " + "FROM products " +"ORDER BY category, name";
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                int price = rs.getInt("price_lbp");
                int stock = rs.getInt("stock_qty");
                int minStock = rs.getInt("min_stock_qty");
                boolean isAddon = rs.getBoolean("is_addon");
                boolean isActive = rs.getBoolean("is_active");

                productsTable.getItems().add(new ProductRow(id, name, category, price, stock, minStock, isAddon, isActive));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearForm() {
        nameField.clear();
        categoryField.clear();
        priceField.clear();
        stockField.clear();
        minStockField.clear();
        addonCheckBox.setSelected(false);
        activeCheckBox.setSelected(true);
    }

    public void setFormError(String message) {
        formError.setText(message);
    }

    public boolean validateInputs() {

        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();
        String minStockText = minStockField.getText().trim();

        if (name.isEmpty() || category.isEmpty() || priceText.isEmpty() || stockText.isEmpty() || minStockText.isEmpty()) {
            formError.setText("Invalid input! Please check all fields.");
            return false;
        }

        formError.setText(" ");
        return true;
    }

}
