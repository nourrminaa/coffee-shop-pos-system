package com.coffeeshop.handlers;

import com.coffeeshop.ui.InventoryView;
import com.coffeeshop.utils.ProductRow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;

import java.sql.Statement;

public class DeleteProductHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TableView<ProductRow> productsTable;
    private InventoryView parent;

    public DeleteProductHandler(Statement st,TableView<ProductRow> productsTable,InventoryView parent) {
        this.st = st;
        this.productsTable = productsTable;
        this.parent = parent;
    }

    @Override
    public void handle(ActionEvent event) {
        ProductRow selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.err.println("ERROR: Select a product to delete.");
            return;
        }
        try {
            String q = "DELETE FROM products WHERE id = " + selected.getId();
            int rows = st.executeUpdate(q);
            if (rows > 0) {
                parent.clearForm();
                parent.loadProducts();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
