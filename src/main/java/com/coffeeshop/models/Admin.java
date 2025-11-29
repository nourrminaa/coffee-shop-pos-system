package com.coffeeshop.models;

public class Admin extends User {
    public Admin(int id, String username, String passwordHash, String displayName) {
        super(id, username, passwordHash, displayName, "ADMIN");
    }
}
