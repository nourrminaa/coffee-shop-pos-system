package com.coffeeshop.observers;

import com.coffeeshop.models.CartItem;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import com.coffeeshop.utils.ThemeUI;

import java.util.ArrayList;

public class ReceiptObserver {

    private VBox receiptItemsBox;
    private Text subtotalValue;
    private Text discountSummaryLabel;
    private Text discountSummaryValue;
    private Text totalValue;

    // this object will be in charge of updating the receiptItemsBox VBox
    // whenever the cart changes -> updateReceipt using obserser
    public ReceiptObserver(VBox receiptItemsBox, Text subtotalValue, Text discountSummaryLabel, Text discountSummaryValue, Text totalValue) {
        this.receiptItemsBox = receiptItemsBox;
        this.subtotalValue = subtotalValue;
        this.discountSummaryLabel = discountSummaryLabel;
        this.discountSummaryValue = discountSummaryValue;
        this.totalValue = totalValue;
    }

    public void updateReceipt(ArrayList<CartItem> cart, int appliedDiscountPercent) {
        receiptItemsBox.getChildren().clear(); // clear box

        int subtotal = 0;
        for (CartItem item : cart) {
            int lineTotal = item.unitPrice * item.quantity;
            subtotal += lineTotal;
            receiptItemsBox.getChildren().add(createReceiptItemRow(item.name, lineTotal + " LBP")); //add to the pane name + total of each product
        }

        int discount = 0;
        if (appliedDiscountPercent > 0) {
            discount = (subtotal * appliedDiscountPercent) / 100; // how much it will be deducted
            discountSummaryLabel.setText("Discount (" + appliedDiscountPercent + "%)"); // change the label to the percentage deducted
            discountSummaryValue.setText("-" + discount + " LBP"); // changed value
        }
        subtotalValue.setText(subtotal + " LBP");
        totalValue.setText((subtotal - discount) + " LBP");
    }

    // when updating the receipt box ui, add each row using this function
    private HBox createReceiptItemRow(String item, String amount) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(3, 0, 3, 0));

        Text itemText = new Text(item);
        itemText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        itemText.setFont(ThemeUI.getFontRegular());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text amountText = new Text(amount);
        amountText.setFill(Color.web(ThemeUI.TEXT_COLOR));
        amountText.setFont(ThemeUI.getFontRegular());

        row.getChildren().addAll(itemText, spacer, amountText);
        return row;
    }
}