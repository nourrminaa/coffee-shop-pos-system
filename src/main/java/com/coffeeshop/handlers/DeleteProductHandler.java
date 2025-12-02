package com.coffeeshop.handlers;

import com.coffeeshop.ui.InventoryView;
import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.models.ProductRow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;

import java.sql.Statement;

public class DeleteProductHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TableView<ProductRow> productsTable;
    private InventoryView parent;
    private OrdersView ordersView;

    public DeleteProductHandler(Statement st,TableView<ProductRow> productsTable,InventoryView parent, OrdersView ordersView) {
        this.st = st;
        this.productsTable = productsTable;
        this.parent = parent;
        this.ordersView = ordersView;
    }

    @Override
    public void handle(ActionEvent event) {
        OrdersView view = new OrdersView();
        if (st == null) {
            view.showWarning("Error!", "Database not connected.");
            return;
        }
        ProductRow selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.err.println("ERROR: Select a product to delete.");
            return;
        }
        try {
            String q = "DELETE FROM products WHERE id = " + selected.getId();
            st.executeUpdate(q);
            parent.clearForm();
            parent.loadProducts();
            ordersView.refreshCategories();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
