package com.coffeeshop.handlers;

import com.coffeeshop.models.PromotionRow;
import com.coffeeshop.ui.OrdersView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

import java.sql.Statement;

public class ApplyDiscountHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private ComboBox<PromotionRow> discountCombo;
    private OrdersView ordersView;

    public ApplyDiscountHandler(Statement st, ComboBox<PromotionRow> discountCombo, OrdersView ordersView) {
        this.st = st;
        this.discountCombo = discountCombo;
        this.ordersView = ordersView;
    }

    @Override
    public void handle(ActionEvent event) {
        PromotionRow selected = discountCombo.getValue();
        if (selected == null) {
            ordersView.showWarning("No promotion selected", "Please select a promotion from the list.");
            return;
        }
        ordersView.setAppliedPromotionId(selected.getId());
        ordersView.setAppliedDiscountPercent(selected.getPercentOff());
        ordersView.refreshCartAndReceipt();
    }
}