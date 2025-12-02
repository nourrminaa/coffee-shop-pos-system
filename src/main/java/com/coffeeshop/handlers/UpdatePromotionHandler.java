package com.coffeeshop.handlers;

import com.coffeeshop.models.PromotionRow;
import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.ui.PromotionsView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Statement;

public class UpdatePromotionHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TableView<PromotionRow> promotionsTable;
    private TextField nameField;
    private TextField percentField;
    private CheckBox activeCheckBox;
    private PromotionsView promotionsView;
    private OrdersView ordersView;

    public UpdatePromotionHandler(Statement st, TableView<PromotionRow> promotionsTable, TextField nameField, TextField percentField, CheckBox activeCheckBox, PromotionsView promotionsView, OrdersView ordersView) {
        this.st = st;
        this.promotionsTable = promotionsTable;
        this.nameField = nameField;
        this.percentField = percentField;
        this.activeCheckBox = activeCheckBox;
        this.promotionsView = promotionsView;
        this.ordersView = ordersView;
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            PromotionRow selected = promotionsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                promotionsView.setFormError("Please select a promotion to update.");
                return;
            }
            if (!promotionsView.validateInputs()) {
                return;
            }
            String name = nameField.getText().trim().replace("'", "''");
            int percent = Integer.parseInt(percentField.getText().trim());
            int isActive = activeCheckBox.isSelected() ? 1 : 0;
            String sql = "UPDATE promotions SET name='" + name + "', percent_off=" + percent + ", is_active=" + isActive + " WHERE id=" + selected.getId();
            st.executeUpdate(sql);
            promotionsView.clearForm();
            promotionsView.loadPromotions();
            if (ordersView != null) {
                ordersView.refreshPromotions();
            }
        } catch (Exception e) {
            promotionsView.setFormError("Error while updating promotion.");
        }
    }
}