package com.coffeeshop.models;

public abstract class User {
    protected int id;
    protected String username;
    protected String passwordHash;
    protected String displayName;
    protected String role;

    public User(int id, String username, String passwordHash, String displayName, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
}
