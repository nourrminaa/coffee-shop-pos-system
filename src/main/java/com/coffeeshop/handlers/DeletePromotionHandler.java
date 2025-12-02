package com.coffeeshop.handlers;

import com.coffeeshop.models.PromotionRow;
import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.ui.PromotionsView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;

import java.sql.Statement;

public class DeletePromotionHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private TableView<PromotionRow> promotionsTable;
    private PromotionsView promotionsView;
    private OrdersView ordersView;

    public DeletePromotionHandler(Statement st, TableView<PromotionRow> promotionsTable, PromotionsView promotionsView, OrdersView ordersView) {
        this.st = st;
        this.promotionsTable = promotionsTable;
        this.promotionsView = promotionsView;
        this.ordersView = ordersView;
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            PromotionRow selected = promotionsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                promotionsView.setFormError("Please select a promotion to delete.");
                return;
            }
            String sql = "DELETE FROM promotions WHERE id=" + selected.getId();
            st.executeUpdate(sql);
            promotionsView.clearForm();
            promotionsView.loadPromotions();
            if (ordersView != null) {
                ordersView.refreshPromotions();
            }
        } catch (Exception e) {
            promotionsView.setFormError("Error while deleting promotion.");
        }
    }
}