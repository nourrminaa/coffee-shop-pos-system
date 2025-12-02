package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.sql.Statement;

public class AddToCartHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private OrdersView view;
    private String name;
    private int unitPrice;
    private int stockQty;
    private int minStockQty;
    private Button addBtn;

    public AddToCartHandler(Statement st, OrdersView view, String name, int unitPrice, int stockQty, int minStockQty, Button addBtn) {
        this.st = st;
        // used to access helper functions
        this.view = view;
        this.name = name;
        this.unitPrice = unitPrice;
        this.stockQty = stockQty;
        this.minStockQty = minStockQty;
        this.addBtn = addBtn;
    }

    @Override
    public void handle(ActionEvent event) {
        if (st == null) {
            view.showWarning("Error!", "Database not connected.");
            return;
        }
        if (stockQty <= 0) {
            view.showWarning("OUT OF STOCK!", name + " is out of stock.");
            return;
        }

        int stock = view.getDbStock(name);
        int qtyInCart = view.getCartQuantity(name);

        // check while adding (after fetching for the first time and checking the db for disabled buttons) if any items would be unavailable after addition
        if (qtyInCart >= stock) {
            view.showWarning("OUT OF STOCK!", name + " is out of stock.");
            addBtn.setDisable(true);
            return;
        }

        // same, check while adding to cart
        if (stock - qtyInCart <= minStockQty) {
            view.showWarning("LOW STOCK", name + " is running low!\nRemaining: " + (stock - qtyInCart));
        }
        view.addToCart(name, unitPrice);
        view.refreshCartAndReceipt();
    }
}