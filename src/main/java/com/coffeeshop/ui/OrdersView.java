package com.coffeeshop.ui;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.coffeeshop.utils.CategoryUtils;
import com.coffeeshop.models.CartItem;
import com.coffeeshop.handlers.AddToCartHandler;
import com.coffeeshop.handlers.CartPlusHandler;
import com.coffeeshop.handlers.CartMinusHandler;
import com.coffeeshop.handlers.CartRemoveHandler;
import com.coffeeshop.handlers.ApplyDiscountHandler;
import com.coffeeshop.handlers.CheckoutHandler;
import com.coffeeshop.handlers.SearchOrdersHandler;
import com.coffeeshop.handlers.LogoutButtonHandler;
import com.coffeeshop.observers.ReceiptObserver;

import com.coffeeshop.models.PromotionRow;

import com.coffeeshop.utils.ThemeUI;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class OrdersView {

    private Statement st;
    private int userId;
    private Stage stage;

    private VBox itemsBox;
    private VBox cartItemsBox;
    private VBox receiptItemsBox;
    private Text subtotalValue;
    private Text discountSummaryLabel;
    private Text discountSummaryValue;
    private Text totalValue;
    private TabPane categoriesTabs;
    private ArrayList<CartItem> cart;
    private Integer appliedPromotionId;
    private int appliedDiscountPercent;
    private ReceiptObserver receiptObserver;

    private ComboBox<PromotionRow> discountCombo;

    public OrdersView() {
        // empty constructor for handlers that only need showWarning()
    }

    public OrdersView(Statement st, Stage stage, int userId) {
        this.st = st;
        this.stage = stage;
        this.userId = userId;
        this.cart = new ArrayList<>();
        this.appliedPromotionId = null;
        this.appliedDiscountPercent = 0;
    }

    public BorderPane getOrdersGUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");
        root.setPadding(new Insets(20));

        HBox searchPane = new HBox(10);
        searchPane.setStyle(ThemeUI.cardStyle());
        searchPane.setPadding(new Insets(12));

        TextField searchField = ThemeUI.createTextField("Search items...");
        searchField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        searchField.setPrefHeight(40);
        searchField.setPrefWidth(970);

        Button searchBtn = ThemeUI.createButton("Search");
        searchBtn.setPrefHeight(40);
        searchBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");

        Button logoutBtn = ThemeUI.createButton("Logout");
        logoutBtn.setPrefHeight(40);
        logoutBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        logoutBtn.setOnAction(new LogoutButtonHandler(st, stage));

        searchPane.getChildren().addAll(searchField, searchBtn, logoutBtn);
        root.setTop(searchPane);

        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20, 0, 0, 0));

        HBox leftPane = new HBox(15);
        leftPane.setStyle(ThemeUI.cardStyle());
        leftPane.setPadding(new Insets(18));

        // get the unique categories from db
        String[] categories = CategoryUtils.loadCategories(st);

        categoriesTabs = new TabPane();
        categoriesTabs.setSide(Side.LEFT);
        ThemeUI.applyTabPaneTheme(categoriesTabs);

        for (String cat : categories) {
            VBox placeholder = new VBox();
            Tab t = new Tab(cat, placeholder);
            t.setClosable(false);
            categoriesTabs.getTabs().add(t);
        }

        categoriesTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            // set an event listener to load the items related to the tab category clicked
            if (newTab != null) {
                loadItems(newTab.getText());
            }
        });

        VBox itemsPane = new VBox(10);
        itemsPane.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");

        Text itemsTitle = new Text("Items");
        itemsTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        itemsTitle.setFont(ThemeUI.getFontBold());

        itemsBox = new VBox(6);

        ScrollPane itemsScroll = new ScrollPane(itemsBox);
        itemsScroll.setFitToWidth(true);
        itemsScroll.setStyle("-fx-background: " + ThemeUI.BG_COLOR + ";" + "-fx-control-inner-background: " + ThemeUI.BG_COLOR + ";" + "-fx-padding: 0;");

        itemsPane.getChildren().addAll(itemsTitle, itemsScroll);
        HBox.setHgrow(itemsPane, Priority.ALWAYS);

        leftPane.getChildren().addAll(categoriesTabs, itemsPane);
        HBox.setHgrow(leftPane, Priority.ALWAYS);

        VBox rightPane = new VBox(15);
        rightPane.setStyle(ThemeUI.cardStyle());
        rightPane.setPadding(new Insets(18));

        VBox cartPane = new VBox(8);
        cartPane.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");

        Text cartTitle = new Text("Current Order");
        cartTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        cartTitle.setFont(ThemeUI.getFontBold());

        cartItemsBox = new VBox(6);
        cartItemsBox.setFillWidth(true);

        ScrollPane cartScroll = new ScrollPane(cartItemsBox);
        cartScroll.setFitToWidth(true);
        cartScroll.setStyle("-fx-background: " + ThemeUI.BG_COLOR + ";" + "-fx-control-inner-background: " + ThemeUI.BG_COLOR + ";" + "-fx-padding: 0;");
        cartPane.getChildren().addAll(cartTitle, cartScroll);

        VBox discountArea = new VBox(4);

        Text discountLabel = new Text("Discount code");
        discountLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        discountLabel.setFont(ThemeUI.getFontRegular());

        HBox discountRow = new HBox(6);

        discountCombo = new ComboBox<>();
        discountCombo.setPrefHeight(28);
        discountCombo.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 13px;");
        discountCombo.setPromptText("Select promotion");
        discountCombo.setMaxWidth(Double.MAX_VALUE);

        Button applyDiscountBtn = ThemeUI.createButton("Apply");
        applyDiscountBtn.setPrefHeight(32);
        applyDiscountBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 12px;");

        HBox.setHgrow(discountCombo, Priority.ALWAYS);
        discountRow.getChildren().addAll(discountCombo, applyDiscountBtn);
        discountArea.getChildren().addAll(discountLabel, discountRow);

        VBox receiptPane = new VBox(8);
        receiptPane.setPadding(new Insets(10, 0, 0, 0));

        Text receiptTitle = new Text("Receipt");
        receiptTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        receiptTitle.setFont(ThemeUI.getFontBold());

        HBox headerRow = new HBox();

        Text colItem = new Text("Item");
        colItem.setFill(Color.web(ThemeUI.TEXT_COLOR));
        colItem.setFont(ThemeUI.getFontRegular());

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Text colAmount = new Text("Amount");
        colAmount.setFill(Color.web(ThemeUI.TEXT_COLOR));
        colAmount.setFont(ThemeUI.getFontRegular());

        headerRow.getChildren().addAll(colItem, headerSpacer, colAmount);

        receiptItemsBox = new VBox(6);
        receiptItemsBox.setFillWidth(true);
        receiptItemsBox.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");

        Region sep1 = createReceiptLine();

        HBox subtotalRow = new HBox();

        Text subtotalLabel = new Text("Subtotal");
        subtotalLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        subtotalLabel.setFont(ThemeUI.getFontRegular());

        Region subtotalSpacer = new Region();
        HBox.setHgrow(subtotalSpacer, Priority.ALWAYS);

        subtotalValue = new Text("0 LBP");
        subtotalValue.setFill(Color.web(ThemeUI.TEXT_COLOR));
        subtotalValue.setFont(ThemeUI.getFontRegular());

        subtotalRow.getChildren().addAll(subtotalLabel, subtotalSpacer, subtotalValue);

        HBox discountSummaryRow = new HBox();

        discountSummaryLabel = new Text("Discount");
        discountSummaryLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        discountSummaryLabel.setFont(ThemeUI.getFontRegular());

        Region discountSummarySpacer = new Region();
        HBox.setHgrow(discountSummarySpacer, Priority.ALWAYS);

        discountSummaryValue = new Text("0 LBP");
        discountSummaryValue.setFill(Color.web(ThemeUI.TEXT_COLOR));
        discountSummaryValue.setFont(ThemeUI.getFontRegular());

        discountSummaryRow.getChildren().addAll(discountSummaryLabel, discountSummarySpacer, discountSummaryValue);

        Region sep2 = createReceiptLine();

        HBox totalRow = new HBox();

        Text totalLabel = new Text("Total");
        totalLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        totalLabel.setFont(ThemeUI.getFontBold());

        Region totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);

        totalValue = new Text("0 LBP");
        totalValue.setFill(Color.web(ThemeUI.TEXT_COLOR));
        totalValue.setFont(ThemeUI.getFontBold());

        totalRow.getChildren().addAll(totalLabel, totalSpacer, totalValue);

        Button checkoutBtn = ThemeUI.createButton("Checkout");
        checkoutBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        checkoutBtn.setMaxWidth(2000);
        checkoutBtn.setPrefHeight(42);

        receiptPane.getChildren().addAll(receiptTitle, headerRow, receiptItemsBox, sep1, subtotalRow, discountSummaryRow, sep2, totalRow, checkoutBtn);

        VBox.setVgrow(cartPane, Priority.ALWAYS);
        rightPane.getChildren().addAll(cartPane, discountArea, receiptPane);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        mainContent.getChildren().addAll(leftPane, rightPane);
        HBox.setHgrow(mainContent, Priority.ALWAYS);
        root.setCenter(mainContent);

        receiptObserver = new ReceiptObserver(receiptItemsBox, subtotalValue, discountSummaryLabel, discountSummaryValue, totalValue);

        // button handlers
        searchBtn.setOnAction(new SearchOrdersHandler(st, searchField, itemsBox, this));
        checkoutBtn.setOnAction(new CheckoutHandler(st, this, userId));
        applyDiscountBtn.setOnAction(new ApplyDiscountHandler(st, discountCombo, this));

        loadItems("All");
        loadPromotions();

        return root;
    }

    public void loadPromotions() {
        if (discountCombo == null) {
            return;
        }
        discountCombo.getItems().clear();
        try {
            String sql = "SELECT id, name, percent_off, is_active FROM promotions WHERE is_active = 1 ORDER BY name";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int percent = rs.getInt("percent_off");
                PromotionRow row = new PromotionRow(id, name, percent, "Yes");
                discountCombo.getItems().add(row);
            }
            rs.close();
        } catch (Exception e) {
        }
    }

    public void refreshPromotions() {
        loadPromotions();
    }

    // items box (VBox) gets filled with item rows (HBox)
    // cart box (VBox) gets filled with cart item rows (HBox)xf
    public void loadItems(String category) {
        try {
            itemsBox.getChildren().clear(); // clear the full box of items
            String sql;

            if (category.equals("All")) {
                sql = "SELECT name, price_lbp, stock_qty, min_stock_qty FROM products WHERE is_active = 1 ORDER BY name";
            } else {
                sql = "SELECT name, price_lbp, stock_qty, min_stock_qty FROM products WHERE is_active = 1 AND category = '" + category + "' ORDER BY name";
            }
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("name");
                // since it is being added to a String,
                // it gets converted to a String automatically
                String priceText = rs.getInt("price_lbp") + " LBP";
                int stock = rs.getInt("stock_qty");
                int min = rs.getInt("min_stock_qty");
                int price = rs.getInt("price_lbp"); // needed in receipt calculations
                // add to the item box each item row created (HBox)
                // (items box in a VBox)
                itemsBox.getChildren().add(createItemRow(name, priceText, stock, min, price));
            }
            rs.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public HBox createItemRow(String name, String price, int stockQty, int minStockQty, int unitPrice) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(6, 0, 6, 0));

        Text nameText = new Text(name);
        nameText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        nameText.setFont(ThemeUI.getFontRegular());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text priceText = new Text(price);
        priceText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        priceText.setFont(ThemeUI.getFontRegular());

        Button addBtn = ThemeUI.createIconButton("plus.png");
        addBtn.setPrefWidth(32);
        addBtn.setPrefHeight(28);
        addBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-padding:0;");

        if (stockQty <= 0) {
            // disabling the button means no action can be made on this button anymore
            addBtn.setDisable(true);
        }

        addBtn.setOnAction(new AddToCartHandler(st, this, name, unitPrice, stockQty, minStockQty, addBtn));

        row.getChildren().addAll(nameText, spacer, priceText, addBtn);
        return row;
    }

    public HBox createCartRow(CartItem item) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(4, 0, 4, 0));

        Text nameText = new Text(item.name);
        nameText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        nameText.setFont(ThemeUI.getFontRegular());

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Text qtyText = new Text("x" + item.quantity);
        qtyText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        qtyText.setFont(ThemeUI.getFontRegular());

        Button minusBtn = ThemeUI.createIconButton("minus.png");
        Button plusBtn = ThemeUI.createIconButton("plus.png");
        Button removeBtn = ThemeUI.createIconButton("remove.png");

        // from here, we can take each item alone based on the item this HBox was created with
        plusBtn.setOnAction(new CartPlusHandler(this, item));
        minusBtn.setOnAction(new CartMinusHandler(this, item));
        removeBtn.setOnAction(new CartRemoveHandler(this, item));

        row.getChildren().addAll(nameText, qtyText, spacer1, minusBtn, plusBtn, removeBtn);
        return row;
    }

    public HBox createReceiptItemRow(String item, String amount) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(3, 0, 3, 0));

        Text itemText = new Text(item);
        itemText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        itemText.setFont(ThemeUI.getFontRegular());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text amountText = new Text(amount);
        amountText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        amountText.setFont(ThemeUI.getFontRegular());

        row.getChildren().addAll(itemText, spacer, amountText);
        return row;
    }

    private Region createReceiptLine() {
        Region line = new Region();
        line.setMaxWidth(2000);
        line.setStyle("-fx-border-color: " + ThemeUI.TEXT_COLOR + ";" + "-fx-border-width: 1 0 0 0;" + "-fx-border-style: segments(8, 8) line-cap butt;");
        return line;
    }

    public void addToCart(String name, int unitPrice) {
        CartItem existing = null;
        // find the cart item
        for (CartItem item : cart) {
            if (item.name.equals(name)) {
                existing = item;
                break;
            }
        }
        // if not added before, create a new cart item with name, unitprice & quantity (default 1)
        if (existing == null) {
            cart.add(new CartItem(name, unitPrice, 1));
        } else {
            // if already exists only increment its quantity
            existing.quantity++;
        }
    }

    public int getCartQuantity(String name) {
        for (CartItem item : cart) {
            if (item.name.equals(name)) {
                return item.quantity;
            }
        }
        return 0;
    }

    public int getDbStock(String name) {
        try {
            String sql = "SELECT stock_qty FROM products WHERE name = '" + name + "'";
            ResultSet rs = st.executeQuery(sql);
            int stock = rs.next() ? rs.getInt("stock_qty") : 0;
            rs.close();
            return stock;
        } catch (Exception e) {
            return 0;
        }
    }

    // create the CartItemBox rows (Hbox) from the CartItem ArrayList
    public void refreshCartAndReceipt() {
        // refresh the cart box UI
        cartItemsBox.getChildren().clear();
        for (CartItem item : cart) {
            cartItemsBox.getChildren().add(createCartRow(item));
        }

        // refresh the receipt
        receiptObserver.updateReceipt(cart, appliedDiscountPercent);
    }

    public void showWarning(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.setGraphic(null); // remove default icon

        // apply custom alert CSS
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());

        alert.getDialogPane().getStyleClass().add("custom-alert-pane");
        alert.getDialogPane().lookup(".header-panel").getStyleClass().add("custom-alert-header");
        alert.getDialogPane().lookup(".content").getStyleClass().add("custom-alert-content");

        // style the OK button
        alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0)).getStyleClass().add("custom-button");
        alert.showAndWait();
    }

    public void refreshCategories() {
        String[] newCategories = CategoryUtils.loadCategories(st);
        categoriesTabs.getTabs().clear();

        for (String cat : newCategories) {
            VBox placeholder = new VBox();
            Tab t = new Tab(cat, placeholder);
            t.getStyleClass().add("category-tab");
            t.setClosable(false);
            categoriesTabs.getTabs().add(t);
        }
        ThemeUI.applyTabPaneTheme(categoriesTabs);
        categoriesTabs.setSkin(null);
        categoriesTabs.applyCss();
        categoriesTabs.getSelectionModel().selectFirst();
        loadItems("All");
    }

    public void clearCart() {
        cart.clear();
    }

    public String getCashierName(Statement st, int userId) {
        try {
            String sql = "SELECT display_name FROM users WHERE id = " + userId + " LIMIT 1";
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                String name = rs.getString("display_name");
                rs.close();
                return name;
            }

            rs.close();
            return "Unknown";

        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
    public ArrayList<CartItem> getCart() {
        return cart;
    }
    public Integer getAppliedPromotionId() {return appliedPromotionId;}
    public void setAppliedPromotionId(Integer id) {
        this.appliedPromotionId = id;
    }
    public int getAppliedDiscountPercent() {return appliedDiscountPercent;}
    public void setAppliedDiscountPercent(int percent) {
        this.appliedDiscountPercent = percent;
    }
}