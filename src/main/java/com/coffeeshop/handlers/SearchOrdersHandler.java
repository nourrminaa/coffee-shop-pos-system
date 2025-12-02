package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.Statement;

public class SearchOrdersHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TextField searchField;
    private VBox itemsBox;
    private OrdersView view;

    public SearchOrdersHandler(Statement st, TextField searchField, VBox itemsBox, OrdersView view) {
        this.st = st;
        this.searchField = searchField;
        this.itemsBox = itemsBox;
        this.view = view;
    }

    @Override
    public void handle(ActionEvent event) {
        OrdersView view = new OrdersView();
        if (st == null) {
            view.showWarning("Error!", "Database not connected.");
            return;
        }
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            view.loadItems("All");
            return;
        }

        try {
            itemsBox.getChildren().clear();

            // if product name contains what is being searched, fetch
            String sql = "SELECT name, price_lbp, stock_qty, min_stock_qty FROM products WHERE is_active = 1 AND name LIKE '%" + query + "%' ORDER BY name";

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("name");
                int price = rs.getInt("price_lbp");
                int stock = rs.getInt("stock_qty");
                int min = rs.getInt("min_stock_qty");
                String priceText = price + " LBP";

                // using the min quantity & stock because it will be needed if the user tries to add an item to cart
                // using the button in the itemRow
                itemsBox.getChildren().add(view.createItemRow(name, priceText, stock, min, price));
            }
            rs.close();

        } catch (Exception e) {
            e.getMessage();
        }
    }
}