package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.utils.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExportPDFHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private ComboBox<String> periodCombo;
    private ComboBox<String> usersCombo;

    public ExportPDFHandler(Statement st, ComboBox<String> periodCombo, ComboBox<String> usersCombo) {
        this.st = st;
        this.periodCombo = periodCombo;
        this.usersCombo = usersCombo;
    }

    @Override
    public void handle(ActionEvent event) {
        OrdersView view = new OrdersView();
        if (st == null) {
            view.showWarning("Error!", "Database not connected.");
            return;
        }
        try {
            // getting the DateRange (from/to) selected in the ComboBox
            LocalDate now = LocalDate.now();
            // customized for date simplification
            DateRange range = new com.coffeeshop.utils.DateRange();

            switch (periodCombo.getValue()) {
                case "Today":
                    range.fromDate = now + " 00:00:00";
                    range.toDate = now + " 23:59:59";
                    range.label = "Today";
                    break;

                case "This Week":
                    LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
                    LocalDate weekEnd = weekStart.plusDays(6);

                    range.fromDate = weekStart + " 00:00:00";
                    range.toDate = weekEnd + " 23:59:59";
                    range.label = "This Week";
                    break;

                case "This Month":
                    LocalDate first = now.withDayOfMonth(1);
                    LocalDate last = now.withDayOfMonth(now.lengthOfMonth());

                    range.fromDate = first + " 00:00:00";
                    range.toDate = last + " 23:59:59";
                    range.label = "This Month";
                    break;

                default:
                    range.fromDate = now + " 00:00:00";
                    range.toDate = now + " 23:59:59";
                    range.label = "Today";
                    break;
            }

            // getting the user selected from ComboBox
            String userLabel = (usersCombo.getValue() == null || usersCombo.getValue().equals("All Users")) ? "All Users" : usersCombo.getValue();

            // craft the user query
            String userSQL = "";
            if (!(usersCombo.getValue() == null || usersCombo.getValue().equals("All Users"))) {
                int id = Integer.parseInt(usersCombo.getValue().split(" - ")[0]);
                userSQL = " AND o.cashier_id = " + id;
            }

            // sales by userid list
            List<SalesByCashierRow> salesList = new ArrayList<>();
            try {
                String q = "SELECT u.display_name, COUNT(o.id) AS c, SUM(o.total_lbp) AS s FROM orders o JOIN users u ON o.cashier_id = u.id WHERE o.created_at >= '" + range.fromDate + "' AND o.created_at <= '" + range.toDate + "' " + userSQL + " GROUP BY u.id ORDER BY s DESC";

                ResultSet rs = st.executeQuery(q);
                while (rs.next()) {
                    SalesByCashierRow row = new SalesByCashierRow();
                    row.cashierName = rs.getString("display_name");
                    row.orderCount = rs.getInt("c");
                    row.totalSales = rs.getInt("s");
                    // add each row to the list
                    salesList.add(row);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // top 10 products list
            List<TopProductRow> productList = new ArrayList<>();
            try {
                String q = "SELECT p.name, p.category, SUM(oi.quantity) AS q, SUM(oi.line_total_lbp) AS t FROM order_items oi JOIN products p ON oi.product_id = p.id JOIN orders o ON oi.order_id = o.id WHERE o.created_at >= '" + range.fromDate + "' AND o.created_at <= '" + range.toDate + "' AND p.is_addon = 0 GROUP BY p.id ORDER BY q DESC LIMIT 10";

                ResultSet rs = st.executeQuery(q);
                while (rs.next()) {
                    TopProductRow row = new TopProductRow();
                    row.productName = rs.getString("name");
                    row.category = rs.getString("category");
                    row.totalQty = rs.getInt("q");
                    row.totalRevenue = rs.getInt("t");
                    productList.add(row);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // inventory status list
            List<InventoryRow> inventoryList = new ArrayList<>();
            try {
                String q = "SELECT name, category, stock_qty, min_stock_qty FROM products WHERE is_active = 1 ORDER BY (stock_qty <= min_stock_qty) DESC, category, name";

                ResultSet rs = st.executeQuery(q);

                while (rs.next()) {
                    InventoryRow row = new InventoryRow();
                    row.productName = rs.getString("name");
                    row.category = rs.getString("category");
                    row.stockQty = rs.getInt("stock_qty");
                    row.minStock = rs.getInt("min_stock_qty");
                    row.status = rs.getString("s");
                    inventoryList.add(row);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // buildig the ReportData attribute that is the data for the Thread PDF
            ReportData data = new ReportData();
            data.periodLabel = range.label;
            data.fromDate = range.fromDate;
            data.toDate = range.toDate;
            data.cashierFilter = userLabel;
            data.salesByCashier = salesList; // made lists
            data.topProducts = productList; // made lists
            data.inventoryItems = inventoryList; // made lists

            // changes according to OS (using linux)
            String name = "Sales_Report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            String path = System.getProperty("user.home") + "/Desktop/" + name; // place in desktop

            // start thread
            new ReportsPDFThread(path, data).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}