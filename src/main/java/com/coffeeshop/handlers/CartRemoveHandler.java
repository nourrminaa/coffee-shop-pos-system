package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.utils.CartItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class CartRemoveHandler implements EventHandler<ActionEvent> {

    private OrdersView view;
    private CartItem item;

    public CartRemoveHandler(OrdersView view, CartItem item) {
        this.view = view;
        this.item = item;
    }

    @Override
    public void handle(ActionEvent event) {
        view.getCart().remove(item); // removing from array
        view.refreshCartAndReceipt();
    }
}