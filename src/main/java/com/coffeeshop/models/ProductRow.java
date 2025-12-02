package com.coffeeshop.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ProductRow {

    private SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleStringProperty category;
    private SimpleIntegerProperty priceLbp;
    private SimpleIntegerProperty stockQty;
    private SimpleIntegerProperty minStockQty;
    private SimpleStringProperty addon;
    private SimpleStringProperty active;

    public ProductRow(int id, String name, String category, int priceLbp, int stockQty, int minStockQty, boolean isAddon, boolean isActive) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.priceLbp = new SimpleIntegerProperty(priceLbp);
        this.stockQty = new SimpleIntegerProperty(stockQty);
        this.minStockQty = new SimpleIntegerProperty(minStockQty);
        this.addon = new SimpleStringProperty(isAddon ? "Yes" : "No");
        this.active = new SimpleStringProperty(isActive ? "Yes" : "No");
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getCategory() { return category.get(); }
    public int getPriceLbp() { return priceLbp.get(); }
    public int getStockQty() { return stockQty.get(); }
    public int getMinStockQty() { return minStockQty.get(); }
    public String getAddon() { return addon.get(); }
    public String getActive() { return active.get(); }
}
