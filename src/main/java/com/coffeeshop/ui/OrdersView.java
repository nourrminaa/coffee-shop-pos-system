package com.coffeeshop.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Application;

public class OrdersView {

    // categories (DB-friendly: later load from DB instead of this array)
    private final String[] categories = {
            "All",
            "Coffee",
            "Tea",
            "Pastries",
            "Cold Drinks",
            "Add-ons"
    };

    // sample items (DB-friendly: later replace with ResultSet mapping)
    // [name, price]
    private final String[][] sampleItems = {
            {"Espresso", "40,000 LBP"},
            {"Cappuccino", "60,000 LBP"},
            {"Latte", "70,000 LBP"},
            {"Americano", "50,000 LBP"},
            {"Butter Croissant", "25,000 LBP"},
            {"Chocolate Muffin", "30,000 LBP"}
    };

    // sample cart rows for UI testing only
    private final String[][] sampleCartItems = {
            {"Espresso", "2", "80,000 LBP"},
            {"Latte", "1", "70,000 LBP"}
    };

    public BorderPane getOrdersGUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");
        root.setPadding(new Insets(20));

        // 1. searchPane
        HBox searchPane = new HBox(10);
        searchPane.setStyle(ThemeUI.cardStyle()); // get the card style (the black rectangle border)
        searchPane.setPadding(new Insets(12));

        TextField searchField = ThemeUI.createTextField("Search items...");
        searchField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;"); // since it is a String we can add to it
        searchField.setPrefHeight(40);
        searchField.setPrefWidth(1100);

        Button searchBtn = ThemeUI.createButton("Search");
        searchBtn.setPrefHeight(40);
        searchBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");

        searchPane.getChildren().addAll(searchField, searchBtn);
        root.setTop(searchPane); // set to the top in the border pane

        // 2. main pane (left pane + right pane)
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20, 0, 0, 0)); // top padding

        // 2.1. left pane
        HBox leftPane = new HBox(15);
        leftPane.setStyle(ThemeUI.cardStyle()); // get the card style (the black rectangle border)
        leftPane.setPadding(new Insets(18));

        // 2.1.1. categories tab pane
        TabPane categoriesTabs = new TabPane();
        // the default side is Top here i need it in the left so i set it manually
        categoriesTabs.setSide(Side.LEFT);
        ThemeUI.applyTabPaneTheme(categoriesTabs);

        for (String cat : categories) {
            VBox placeholder = new VBox(); // TO BE CHANGED TO THE TAB THAT WILL BE SEEN
            Tab t = new Tab(cat, placeholder);
            t.setClosable(false);
            categoriesTabs.getTabs().add(t);
        }

        // 2.1.2. items pane
        VBox itemsPane = new VBox(10);
        itemsPane.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");

        Text itemsTitle = new Text("Items");
        itemsTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        itemsTitle.setFont(ThemeUI.getFontBold());

        VBox itemsBox = new VBox(6);
        for (String[] item : sampleItems) {
            itemsBox.getChildren().add(createItemRow(item[0], item[1])); // because item = {"Espresso", "5.00"}
        } // create item row: takes the item + price and adds a + button

        ScrollPane itemsScroll = new ScrollPane(itemsBox); // to make the items box pane scrollable
        itemsScroll.setFitToWidth(true); // expand as needed
        itemsScroll.setStyle("-fx-background: " + ThemeUI.BG_COLOR + ";" + "-fx-control-inner-background: " + ThemeUI.BG_COLOR + ";" +"-fx-padding: 0;");

        itemsPane.getChildren().addAll(itemsTitle, itemsScroll);
        HBox.setHgrow(itemsPane, Priority.ALWAYS); // make the items pane expand as needed

        leftPane.getChildren().addAll(categoriesTabs, itemsPane);
        HBox.setHgrow(leftPane, Priority.ALWAYS); // make the left pane expand as needed

        // 2.2. right pane
        VBox rightPane = new VBox(15);
        rightPane.setStyle(ThemeUI.cardStyle());
        rightPane.setPadding(new Insets(18));

        // 2.2.1. cart pane
        VBox cartPane = new VBox(8);
        cartPane.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");

        Text cartTitle = new Text("Current Order");
        cartTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        cartTitle.setFont(ThemeUI.getFontBold());

        VBox cartItemsBox = new VBox(6);
        cartItemsBox.setFillWidth(true); // to expand

        for (String[] cartRow : sampleCartItems) {
            cartItemsBox.getChildren().add(createCartRow(cartRow[0], cartRow[1]));
        } // name, qty, -, +, x

        ScrollPane cartScroll = new ScrollPane(cartItemsBox); // put the cartItemsBox in scrollable mode
        cartScroll.setFitToWidth(true);
        cartScroll.setStyle("-fx-background: " + ThemeUI.BG_COLOR + ";" +"-fx-control-inner-background: " + ThemeUI.BG_COLOR + ";" +"-fx-padding: 0;");
        cartPane.getChildren().addAll(cartTitle, cartScroll);

        // 2.2.2. discount pane
        VBox discountArea = new VBox(4);

        Text discountLabel = new Text("Discount code");
        discountLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        discountLabel.setFont(ThemeUI.getFontRegular());

        HBox discountRow = new HBox(6);

        TextField discountField = ThemeUI.createTextField("Enter code");
        discountField.setPrefHeight(32);
        discountField.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 13px;");

        Button applyDiscountBtn = ThemeUI.createButton("Apply");
        applyDiscountBtn.setPrefHeight(32);
        applyDiscountBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 12px;");

        HBox.setHgrow(discountField, Priority.ALWAYS); // expand the discound field
        discountRow.getChildren().addAll(discountField, applyDiscountBtn);
        discountArea.getChildren().addAll(discountLabel, discountRow);

        // 2.2.3. receipt pane
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
        // header spacer is used to add space between item & amound
        headerRow.getChildren().addAll(colItem, headerSpacer, colAmount);

        // 2.2.3.1. receipt items pane
        VBox receiptItemsBox = new VBox(6);
        receiptItemsBox.setFillWidth(true);
        receiptItemsBox.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");

        for (String[] row : sampleCartItems) {
            receiptItemsBox.getChildren().add(createReceiptItemRow(row[0], row[2]));
        }

        Region sep1 = createReceiptLine();

        HBox subtotalRow = new HBox();

        Text subtotalLabel = new Text("Subtotal");
        subtotalLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        subtotalLabel.setFont(ThemeUI.getFontRegular());

        Region subtotalSpacer = new Region();
        HBox.setHgrow(subtotalSpacer, Priority.ALWAYS);

        Text subtotalValue = new Text("150,000 LBP");
        subtotalValue.setFill(Color.web(ThemeUI.TEXT_COLOR));
        subtotalValue.setFont(ThemeUI.getFontRegular());

        subtotalRow.getChildren().addAll(subtotalLabel, subtotalSpacer, subtotalValue);

        HBox discountSummaryRow = new HBox();

        Text discountSummaryLabel = new Text("Discount (10%)");
        discountSummaryLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        discountSummaryLabel.setFont(ThemeUI.getFontRegular());

        Region discountSummarySpacer = new Region();
        HBox.setHgrow(discountSummarySpacer, Priority.ALWAYS);

        Text discountSummaryValue = new Text("-15,000 LBP");
        discountSummaryValue.setFill(Color.web(ThemeUI.TEXT_COLOR));
        discountSummaryValue.setFont(ThemeUI.getFontRegular());

        discountSummaryRow.getChildren().addAll(discountSummaryLabel,discountSummarySpacer,discountSummaryValue);

        Region sep2 = createReceiptLine();

        HBox totalRow = new HBox();

        Text totalLabel = new Text("Total");
        totalLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        totalLabel.setFont(ThemeUI.getFontBold());

        Region totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);

        Text totalValue = new Text("150,000 LBP");
        totalValue.setFill(Color.web(ThemeUI.TEXT_COLOR));
        totalValue.setFont(ThemeUI.getFontBold());

        totalRow.getChildren().addAll(totalLabel, totalSpacer, totalValue);

        Button checkoutBtn = ThemeUI.createButton("Checkout");
        checkoutBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        checkoutBtn.setMaxWidth(2000);
        checkoutBtn.setPrefHeight(42);

        receiptPane.getChildren().addAll(receiptTitle,headerRow,receiptItemsBox, sep1,subtotalRow,discountSummaryRow,sep2,totalRow,checkoutBtn);

        VBox.setVgrow(cartPane, Priority.ALWAYS);
        rightPane.getChildren().addAll(cartPane, discountArea, receiptPane);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        mainContent.getChildren().addAll(leftPane, rightPane);
        HBox.setHgrow(mainContent, Priority.ALWAYS);
        root.setCenter(mainContent);

        return root;
    }

    // create product item row
    private HBox createItemRow(String name, String price) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(6, 0, 6, 0)); // top & bottom padding

        Text nameText = new Text(name);
        nameText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        nameText.setFont(ThemeUI.getFontRegular());

        Region spacer = new Region(); // this is an invisible node that expands between the name & price
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text priceText = new Text(price);
        priceText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        priceText.setFont(ThemeUI.getFontRegular());

        Button addBtn = ThemeUI.createButton("+");
        addBtn.setPrefWidth(32);
        addBtn.setPrefHeight(28);
        addBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;" + "-fx-padding: 2 6;");

        row.getChildren().addAll(nameText, spacer, priceText, addBtn);
        return row;
    }

    // create cart row
    private HBox createCartRow(String name, String qty) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(4, 0, 4, 0));

        Text nameText = new Text(name);
        nameText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        nameText.setFont(ThemeUI.getFontRegular());

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Text qtyText = new Text("x" + qty);
        qtyText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        qtyText.setFont(ThemeUI.getFontRegular());

        Button minusBtn = ThemeUI.createButton("-");
        minusBtn.setPrefWidth(28);
        minusBtn.setPrefHeight(26);
        minusBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 13px;" + "-fx-padding: 2 6;");

        Button plusBtn = ThemeUI.createButton("+");
        plusBtn.setPrefWidth(28);
        plusBtn.setPrefHeight(26);
        plusBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 13px;" + "-fx-padding: 2 6;");

        Button removeBtn = ThemeUI.createButton("x");
        removeBtn.setPrefWidth(28);
        removeBtn.setPrefHeight(26);
        removeBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 13px;" + "-fx-padding: 2 6;");

        row.getChildren().addAll(nameText,qtyText,spacer1, minusBtn, plusBtn, removeBtn);
        return row;
    }

    // create receipt item row
    private HBox createReceiptItemRow(String item, String amount) {
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
        line.setStyle("-fx-border-color: " + ThemeUI.TEXT_COLOR + ";" + "-fx-border-width: 1 0 0 0;" +"-fx-border-style: segments(8, 8) line-cap butt;");
        return line;
    }

//    @Override
//    public void start(Stage primaryStage) {
//        OrdersView view = new OrdersView();
//        BorderPane root = view.getOrdersGUI();
//
//        Scene scene = new Scene(root, 1400, 800);
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("CoffeeShop POS - Orders");
//        primaryStage.setFullScreen(true);
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
}
