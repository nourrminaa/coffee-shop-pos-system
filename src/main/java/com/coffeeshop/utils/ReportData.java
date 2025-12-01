package com.coffeeshop.utils;

import java.util.List;

public class ReportData {

    public String periodLabel;
    public String fromDate;
    public String toDate;
    public String cashierFilter;

    // since in same package no need to import the classes
    public List<SalesByCashierRow> salesByCashier;
    public List<TopProductRow> topProducts;
    public List<InventoryRow> inventoryItems;
}
