package com.coffeeshop.handlers;

import com.coffeeshop.ui.InventoryView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import com.coffeeshop.utils.ProductRow;

import java.sql.Statement;

public class AddProductHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TextField nameField;
    private TextField categoryField;
    private TextField priceField;
    private TextField stockField;
    private TextField minStockField;
    private CheckBox addonCheckBox;
    private CheckBox activeCheckBox;
    private InventoryView parent;

    public AddProductHandler(Statement st, TextField nameField, TextField categoryField, TextField priceField, TextField stockField, TextField minStockField, CheckBox addonCheckBox, CheckBox activeCheckBox, InventoryView parent) {
        this.st = st;
        this.nameField = nameField;
        this.categoryField = categoryField;
        this.priceField = priceField;
        this.stockField = stockField;
        this.minStockField = minStockField;
        this.addonCheckBox = addonCheckBox;
        this.activeCheckBox = activeCheckBox;
        this.parent = parent;
    }

    @Override
    public void handle(ActionEvent event) {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();
        String minStockText = minStockField.getText().trim();
        boolean isAddon = addonCheckBox.isSelected();
        boolean isActive = activeCheckBox.isSelected();

        if (name.isEmpty() || category.isEmpty() || priceText.isEmpty() || stockText.isEmpty() || minStockText.isEmpty()) {
            System.err.println("ERROR: All fields are required for adding product.");
            return;
        }

        try {
            int price = Integer.parseInt(priceText);
            int stock = Integer.parseInt(stockText);
            int minStock = Integer.parseInt(minStockText);

            String insert = "INSERT INTO products (name, category, price_lbp, stock_qty, min_stock_qty, is_addon, is_active) VALUES (" + "'" + name + "', " + "'" + category + "', " + price + ", " + stock + ", " + minStock + ", " + (isAddon ? 1 : 0) + ", " + (isActive ? 1 : 0) + ")";
            int rows = st.executeUpdate(insert);
            // update table
            if (rows > 0) {
                parent.clearForm();
                parent.loadProducts();
            }
        } catch (NumberFormatException nfe) {
            System.err.println("ERROR: Price, Stock, and Min Stock must be numbers.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}