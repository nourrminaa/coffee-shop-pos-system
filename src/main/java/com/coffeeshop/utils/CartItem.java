package com.coffeeshop.utils;

public class CartItem {
    public String name;
    public int unitPrice;
    public int quantity;

    public CartItem(String name, int unitPrice, int quantity) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
}
