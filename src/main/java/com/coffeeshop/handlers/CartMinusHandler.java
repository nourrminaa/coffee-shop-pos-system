package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.utils.CartItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class CartMinusHandler implements EventHandler<ActionEvent> {

    private OrdersView view;
    private CartItem item;

    public CartMinusHandler(OrdersView view, CartItem item) {
        this.view = view;
        this.item = item;
    }

    @Override
    public void handle(ActionEvent event) {
        item.quantity--;
        if (item.quantity <= 0) {
            view.getCart().remove(item);
        }
        view.refreshCartAndReceipt();
    }
}