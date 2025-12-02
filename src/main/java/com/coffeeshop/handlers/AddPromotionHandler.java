package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.ui.PromotionsView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.sql.Statement;

public class AddPromotionHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TextField nameField;
    private TextField percentField;
    private CheckBox activeCheckBox;
    private PromotionsView promotionsView;
    private OrdersView ordersView;

    public AddPromotionHandler(Statement st, TextField nameField, TextField percentField, CheckBox activeCheckBox, PromotionsView promotionsView, OrdersView ordersView) {
        this.st = st;
        this.nameField = nameField;
        this.percentField = percentField;
        this.activeCheckBox = activeCheckBox;
        this.promotionsView = promotionsView;
        this.ordersView = ordersView;
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            if (!promotionsView.validateInputs()) {
                return;
            }
            String name = nameField.getText().trim().replace("'", "''");
            int percent = Integer.parseInt(percentField.getText().trim());
            int isActive = activeCheckBox.isSelected() ? 1 : 0;
            String sql = "INSERT INTO promotions (name, percent_off, is_active) VALUES ('" + name + "', " + percent + ", " + isActive + ")";
            st.executeUpdate(sql);
            promotionsView.clearForm();
            promotionsView.loadPromotions();
            if (ordersView != null) {
                ordersView.refreshPromotions();
            }
        } catch (Exception e) {
            promotionsView.setFormError("Error while saving promotion.");
        }
    }
}