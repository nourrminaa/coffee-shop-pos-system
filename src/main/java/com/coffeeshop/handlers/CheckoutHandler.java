package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.models.CartItem;
import com.coffeeshop.utils.ReceiptPDFThread;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CheckoutHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private OrdersView view;
    private int userId;

    public CheckoutHandler(Statement st, OrdersView view, int userId) {
        this.st = st;
        this.view = view;
        this.userId = userId;
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            if (view.getCart().isEmpty()) {
                view.showWarning("Checkout Error", "Cart is empty!");
                return;
            }

            int subtotal = 0;
            for (CartItem item : view.getCart()) {
                subtotal += item.unitPrice * item.quantity;
            }

            int discount = view.getAppliedPromotionId() == null ? 0 : (subtotal * view.getAppliedDiscountPercent()) / 100;
            int total = subtotal - discount;

            // set promoId
            String promoValue = (view.getAppliedPromotionId() == null) ? "NULL" : view.getAppliedPromotionId().toString();

            // insert order query
            String orderSql = "INSERT INTO orders (cashier_id, promotion_id, discount_lbp, total_lbp) VALUES (" + userId + ", " + promoValue + ", " + discount + ", " + total + ")";
            // Statement.RETURN_GENERATED_KEYS is used to get the newly generated order row because we need it for the order_item insertion
            st.executeUpdate(orderSql, Statement.RETURN_GENERATED_KEYS);

            ResultSet keys = st.getGeneratedKeys();
            // if keys.next() -> a line has been added so get the col 1 (order_id)
            int orderId = keys.next() ? keys.getInt(1) : 0;
            keys.close();

            // for each item in cart -> insert in db under order items
            for (CartItem item : view.getCart()) {

                // get product id
                String prodSql = "SELECT id FROM products WHERE name = '" + item.name + "' LIMIT 1";
                ResultSet rsProd = st.executeQuery(prodSql);

                // if nothing selected skip item with 'continue'
                if (!rsProd.next()) {
                    rsProd.close();
                    continue;
                }

                int productId = rsProd.getInt("id");
                rsProd.close();

                // insert order item query
                int lineTotal = item.unitPrice * item.quantity;
                String itemSql = "INSERT INTO order_items (order_id, product_id, unit_price_lbp, quantity, line_total_lbp) VALUES (" + orderId + ", " + productId + ", " + item.unitPrice + ", " + item.quantity + ", " + lineTotal + ")";
                st.executeUpdate(itemSql);

                // update stock query
                String updateStockSql = "UPDATE products SET stock_qty = stock_qty - " + item.quantity + " WHERE id = " + productId;
                st.executeUpdate(updateStockSql);
            }

            // keep the version before resetting ui
            ArrayList<CartItem> receiptItems = new ArrayList<>(view.getCart());
            String savePath = System.getProperty("user.home") + "/Desktop/" + orderId + ".pdf";
            new ReceiptPDFThread(savePath, orderId, view.getCashierName(st, userId), receiptItems, total)
                    .start();


            // reset UI
            view.clearCart();
            view.setAppliedPromotionId(null);
            view.setAppliedDiscountPercent(0);
            view.refreshCartAndReceipt();

        } catch (Exception e) {
            view.showWarning("Checkout Error", e.getMessage());
        }
    }
}