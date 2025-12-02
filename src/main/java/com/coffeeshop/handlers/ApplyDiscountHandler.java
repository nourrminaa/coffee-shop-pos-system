package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ApplyDiscountHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TextField discountField;
    private OrdersView view;

    public ApplyDiscountHandler(Statement st, TextField discountField, OrdersView view) {
        this.st = st;
        this.discountField = discountField;
        this.view = view;
    }

    @Override
    public void handle(ActionEvent event) {
        if (st == null) {
            view.showWarning("Error!", "Database not connected.");
            return;
        }

        String entered = discountField.getText().trim();
        if (entered.isEmpty()) {
            view.showWarning("No Code", "Please enter a promotion name.");
            return;
        }

        try {
            String sql = "SELECT id, percent_off FROM promotions WHERE name = '" + entered + "' AND is_active = 1";
            ResultSet rs = st.executeQuery(sql);

            if (!rs.next()) {
                view.showWarning("Invalid Promotion", "No active promotion found");
            } else {
                view.setAppliedPromotionId(rs.getInt("id"));
                view.setAppliedDiscountPercent(rs.getInt("percent_off"));
                view.refreshCartAndReceipt();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}