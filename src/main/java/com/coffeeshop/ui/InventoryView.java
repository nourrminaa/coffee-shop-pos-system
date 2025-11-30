package com.coffeeshop.ui;

import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Statement;

public class InventoryView {
    private Statement st;
    private Stage stage;

    public InventoryView(Statement st, Stage stage) {
        this.st = st;
        this.stage = stage;
    }
    public HBox getInventoryGUI() {
        return new HBox();
    }
}
