package com.coffeeshop.ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserRow {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty username;
    private final SimpleStringProperty displayName;
    private final SimpleStringProperty role;
    private final SimpleStringProperty createdAt;

    public UserRow(int id, String username, String displayName, String role, String createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.displayName = new SimpleStringProperty(displayName);
        this.role = new SimpleStringProperty(role);
        this.createdAt = new SimpleStringProperty(createdAt);
    }

    public int getId() { return id.get(); }
    public String getUsername() { return username.get(); }
    public String getDisplayName() { return displayName.get(); }
    public String getRole() { return role.get(); }
    public String getCreatedAt() { return createdAt.get(); }
}