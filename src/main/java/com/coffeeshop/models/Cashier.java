package com.coffeeshop.models;

public class Cashier extends User {
    public Cashier(int id, String username, String passwordHash, String displayName) {
        super(id, username, passwordHash, displayName, "CASHIER");
    }
}
