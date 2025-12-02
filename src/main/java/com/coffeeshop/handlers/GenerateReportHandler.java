package com.coffeeshop.handlers;

import com.coffeeshop.ui.OrdersView;
import com.coffeeshop.models.DateRange;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class GenerateReportHandler implements EventHandler<ActionEvent> {

    private Statement st;
    private ComboBox<String> periodCombo;
    private ComboBox<String> usersCombo;
    private TextArea preview;

    public GenerateReportHandler(Statement st, ComboBox<String> periodCombo, ComboBox<String> usersCombo, TextArea preview) {
        this.st = st;
        this.periodCombo = periodCombo;
        this.usersCombo = usersCombo;
        this.preview = preview;
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            OrdersView view = new OrdersView();
            if (st == null) {
                view.showWarning("Error!", "Database not connected.");
                return;
            }
            // getting the DateRange (from/to) selected in the ComboBox
            LocalDate now = LocalDate.now();
            // customized for date simplification
            DateRange range = new DateRange();

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
            String userSQL = new String();
            if (usersCombo.getValue() == null ||usersCombo.getValue().equals("All Users"))
                userSQL = "";
            else {
                int id = Integer.parseInt(usersCombo.getValue().split(" - ")[0]);
                userSQL=  " AND o.cashier_id = " + id;
            }

            // sales text in TextArea
            String sales = new String();
            try {
                String q = "SELECT u.display_name, COUNT(o.id) AS c, SUM(o.total_lbp) AS s FROM orders o JOIN users u ON o.cashier_id = u.id WHERE DATE(o.created_at) >= '" + range.fromDate + "' AND DATE(o.created_at) <= '" + range.toDate + "' " + userSQL + " GROUP BY u.id ORDER BY s DESC";

                ResultSet rs = st.executeQuery(q);
                boolean has = false;

                while (rs.next()) {
                    has = true;
                    String name = rs.getString("display_name");
                    int c = rs.getInt("c");
                    int s = rs.getInt("s");

                    // pad columns so | always align (-20 is the padding and %s -> String, %d -> int)
                    sales += String.format("%-50s | Orders: %-50d | Sales: %d LBP", name, c, s);
                }
                // if no sales for cashier/admin
                if (!has) sales += "No sales.\n";
            } catch (Exception e) {
                sales +=  "Error! \n";
            }

            // top 10 product in TextArea
            String topProduct = new String();
            try {
                String q = "SELECT p.name, p.category, SUM(oi.quantity) q, SUM(oi.line_total_lbp) t FROM order_items oi JOIN products p ON oi.product_id = p.id JOIN orders o ON oi.order_id = o.id WHERE DATE(o.created_at) >= '" + range.fromDate + "' AND DATE(o.created_at) <= '" + range.toDate + "' AND p.is_addon = 0 GROUP BY p.id ORDER BY q DESC LIMIT 10";
                ResultSet rs = st.executeQuery(q);

                int rank = 1;
                boolean has = false;
                while (rs.next()) {
                    has = true;
                    String name = rs.getString("name");
                    String category = rs.getString("category");
                    int qVal = rs.getInt("q");
                    int tVal = rs.getInt("t");

                    topProduct += String.format("%d. %s (%s) %-50s| Qty: %-50d | Revenue: %d LBP\n", rank++, name, category,"", qVal, tVal);
                }
                if (!has) topProduct += "No product sales.\n";
            } catch (Exception e) {
                topProduct += "Error! \n";
            }

            // inventory status in TextArea
            String inventoryStatus = new String();
            try {
                String q = "SELECT name, category, stock_qty, min_stock_qty FROM products WHERE is_active = 1 ORDER BY category, name";
                ResultSet rs = st.executeQuery(q);

                while (rs.next()) {
                    String icon = (rs.getInt("stock_qty") <= rs.getInt("min_stock_qty")) ? "[!]" : "   ";
                    String name = rs.getString("name");
                    String category = rs.getString("category");
                    int stock = rs.getInt("stock_qty");
                    int min = rs.getInt("min_stock_qty");

                    inventoryStatus += String.format("%-3s %s (%s) %-50s| Stock: %-50d | Min: %d\n", icon, name, category,"", stock, min);
                }
            } catch (Exception e) {
                inventoryStatus += "Error! \n";
            }

            // craft the placeholder text
            String out = "";
            out += "========================================\n";
            out += "       COFFEE SHOP SALES REPORT\n";
            out += "========================================\n\n";
            out += "Period: " + range.label + "\n";
            out += "From: " + range.fromDate + "\n";
            out += "To: " + range.toDate + "\n\n";
            out += "User: " + userLabel + "\n";
            out += "========================================\n\n";

            out += "SALES BY USER:\n";
            out += "----------------------------------------\n";
            out += sales + "\n";

            out += "TOP SELLING PRODUCTS:\n";
            out += "----------------------------------------\n";
            out += topProduct + "\n";

            out += "INVENTORY STATUS:\n";
            out += "----------------------------------------\n";
            out += inventoryStatus + "\n";

            preview.setText(out);

        } catch (Exception e) {
            preview.setText("Error generating report!\n");
        }
    }
}