package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.utils.CartItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.sql.Statement;

public class CartPlusHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private OrdersView view;
    private CartItem item;

    public CartPlusHandler(OrdersView view, CartItem item) {
        this.view = view;
        this.item = item;
    }

    @Override
    public void handle(ActionEvent event) {
        int stock = view.getDbStock(item.name);
        int qtyInCart = view.getCartQuantity(item.name);

        if (qtyInCart >= stock) {
            view.showWarning("OUT OF STOCK", item.name + " is out of stock!");
            return;
        }

        // CartItem Class
        item.quantity++;
        view.refreshCartAndReceipt();
    }
}