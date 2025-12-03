package com.coffeeshop.handlers;

import com.coffeeshop.ui.InventoryView;
import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.models.ProductRow;
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
    private OrdersView ordersView;

    public UpdateProductHandler(Statement st, TableView<ProductRow> productsTable, TextField nameField, TextField categoryField, TextField priceField, TextField stockField, TextField minStockField, CheckBox addonCheckBox, CheckBox activeCheckBox, InventoryView parent, OrdersView ordersView) {
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
        this.ordersView = ordersView;
    }

    @Override
    public void handle(ActionEvent event) {
        if (st == null) {
            ordersView.showWarning("Error!", "Database not connected.");
            return;
        }

        ProductRow selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            parent.setFormError("Select a product first.");
            return;
        }

        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();
        String minStockText = minStockField.getText().trim();
        boolean addon = addonCheckBox.isSelected();
        boolean active = activeCheckBox.isSelected();

        if (!parent.validateInputs()) return;

        try {
            int price = Integer.parseInt(priceText);
            int stock = Integer.parseInt(stockText);
            int minStock = Integer.parseInt(minStockText);

            String q = "UPDATE products SET " + "name = '" + name + "', " + "category = '" + category + "', " + "price_lbp = " + price + ", " + "stock_qty = " + stock + ", " + "min_stock_qty = " + minStock + ", " + "is_addon = " + (addon ? 1 : 0) + ", " + "is_active = " + (active ? 1 : 0) + " WHERE id = " + selected.getId();
            st.executeUpdate(q);
            parent.clearForm();
            parent.loadProducts();
            this.ordersView.refreshCategories();
        } catch (NumberFormatException ex) {
            parent.setFormError("Invalid numeric values.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
