package com.coffeeshop.handlers;

import com.coffeeshop.ui.InventoryView;
import com.coffeeshop.utils.ProductRow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Statement;

public class UpdateProductHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TableView<ProductRow> productsTable;
    private TextField nameField;
    private TextField categoryField;
    private TextField priceField;
    private TextField stockField;
    private TextField minStockField;
    private CheckBox addonCheckBox;
    private CheckBox activeCheckBox;
    private InventoryView parent;

    public UpdateProductHandler(Statement st, TableView<ProductRow> productsTable, TextField nameField, TextField categoryField, TextField priceField, TextField stockField, TextField minStockField, CheckBox addonCheckBox, CheckBox activeCheckBox, InventoryView parent) {
        this.st = st;
        this.productsTable = productsTable;
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
        ProductRow selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.err.println("ERROR: Select a product first.");
            return;
        }
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();
        String minStockText = minStockField.getText().trim();
        boolean addon = addonCheckBox.isSelected();
        boolean active = activeCheckBox.isSelected();

        if (name.isEmpty() || category.isEmpty() || priceText.isEmpty() || stockText.isEmpty() || minStockText.isEmpty()) {
            System.err.println("ERROR: All fields are required for updating product.");
            return;
        }

        try {
            int price = Integer.parseInt(priceText);
            int stock = Integer.parseInt(stockText);
            int minStock = Integer.parseInt(minStockText);

            String q = "UPDATE products SET " + "name = '" + name + "', " + "category = '" + category + "', " + "price_lbp = " + price + ", " + "stock_qty = " + stock + ", " + "min_stock_qty = " + minStock + ", " + "is_addon = " + (addon ? 1 : 0) + ", " + "is_active = " + (active ? 1 : 0) + " WHERE id = " + selected.getId();
            int rows = st.executeUpdate(q);
            if (rows > 0) {
                parent.clearForm();
                parent.loadProducts();
            }
        } catch (NumberFormatException ex) {
            System.err.println("ERROR: Price, Stock, and Min Stock must be numbers.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
