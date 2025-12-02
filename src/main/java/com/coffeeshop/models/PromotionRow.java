package com.coffeeshop.models;

public class PromotionRow {

    private int id;
    private String name;
    private int percentOff;
    private String active;

    public PromotionRow(int id, String name, int percentOff, String active) {
        this.id = id;
        this.name = name;
        this.percentOff = percentOff;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPercentOff() {
        return percentOff;
    }

    public String getActive() {
        return active;
    }

    @Override
    public String toString() {
        return name + " - " + percentOff + "%";
    }
}