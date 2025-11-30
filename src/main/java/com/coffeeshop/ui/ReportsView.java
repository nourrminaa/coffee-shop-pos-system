package com.coffeeshop.ui;

import com.coffeeshop.handlers.LogoutButtonHandler;
import com.coffeeshop.utils.ReportsPDFThread;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportsView {

    private Statement st;
    private Stage stage;
    private ComboBox<String> usersComboBox;
    private ComboBox<String> periodComboBox;
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private TextArea reportPreviewArea;

    public ReportsView() {
        this.st = null;
    }

    public ReportsView(Statement st, Stage stage) {
        this.st = st;
        this.stage = stage;
    }

    public BorderPane getReportsGUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + ThemeUI.BG_COLOR + ";");
        root.setPadding(new Insets(20));

        // Header
        HBox headerPane = new HBox(10);
        headerPane.setStyle(ThemeUI.cardStyle());
        headerPane.setPadding(new Insets(12));
        headerPane.setAlignment(Pos.CENTER_LEFT);

        Text headerTitle = new Text("Reports & Analytics");
        headerTitle.setFill(Color.web(ThemeUI.TEXT_COLOR));
        headerTitle.setFont(ThemeUI.getFontBold());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = ThemeUI.createButton("Logout");
        logoutBtn.setPrefHeight(40);
        logoutBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        logoutBtn.setOnAction(new LogoutButtonHandler(st, stage));

        headerPane.getChildren().addAll(headerTitle, spacer, logoutBtn);
        root.setTop(headerPane);

        // Main content area
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20, 0, 0, 0));

        // Left panel - Report Configuration
        VBox leftPanel = createReportConfigPanel();

        // Right panel - Preview & Inventory Status
        VBox rightPanel = createPreviewPanel();

        leftPanel.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.35));
        rightPanel.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.65));

        mainContent.getChildren().addAll(leftPanel, rightPanel);
        root.setCenter(mainContent);

        return root;
    }

    private VBox createReportConfigPanel() {
        VBox panel = new VBox(20);
        panel.setStyle(ThemeUI.cardStyle());
        panel.setPadding(new Insets(18));

        Text title = new Text("Report Configuration");
        title.setFill(Color.web(ThemeUI.TEXT_COLOR));
        title.setFont(ThemeUI.getFontBold());

        // Period selection
        VBox periodBox = new VBox(6);
        Text periodLabel = new Text("Report Period");
        periodLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        periodLabel.setFont(ThemeUI.getFontRegular());
        periodLabel.setStyle("-fx-font-size: 14px;");

        periodComboBox = new ComboBox<>();
        periodComboBox.getItems().addAll("Today", "This Week", "This Month", "Custom Range");
        periodComboBox.setValue("Today");
        periodComboBox.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        periodComboBox.setPrefHeight(38);
        periodComboBox.setMaxWidth(Double.MAX_VALUE);
        periodComboBox.setOnAction(e -> toggleDatePickers());

        periodBox.getChildren().addAll(periodLabel, periodComboBox);

        // Custom date range (initially hidden)
        VBox dateRangeBox = new VBox(12);
        dateRangeBox.setManaged(false);
        dateRangeBox.setVisible(false);

        VBox fromBox = new VBox(6);
        Text fromLabel = new Text("From Date");
        fromLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        fromLabel.setFont(ThemeUI.getFontRegular());
        fromLabel.setStyle("-fx-font-size: 14px;");

        fromDatePicker = new DatePicker(LocalDate.now().minusDays(7));
        fromDatePicker.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        fromDatePicker.setPrefHeight(38);
        fromDatePicker.setMaxWidth(Double.MAX_VALUE);

        fromBox.getChildren().addAll(fromLabel, fromDatePicker);

        VBox toBox = new VBox(6);
        Text toLabel = new Text("To Date");
        toLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        toLabel.setFont(ThemeUI.getFontRegular());
        toLabel.setStyle("-fx-font-size: 14px;");

        toDatePicker = new DatePicker(LocalDate.now());
        toDatePicker.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        toDatePicker.setPrefHeight(38);
        toDatePicker.setMaxWidth(Double.MAX_VALUE);

        toBox.getChildren().addAll(toLabel, toDatePicker);

        dateRangeBox.getChildren().addAll(fromBox, toBox);

        // User filter
        VBox cashierBox = new VBox(6);
        Text cashierLabel = new Text("Filter by User");
        cashierLabel.setFill(Color.web(ThemeUI.TEXT_COLOR));
        cashierLabel.setFont(ThemeUI.getFontRegular());
        cashierLabel.setStyle("-fx-font-size: 14px;");

        usersComboBox = new ComboBox<>();
        usersComboBox.getItems().add("All Users");
        usersComboBox.setValue("All Users");
        usersComboBox.setStyle(ThemeUI.textFieldStyle() + "-fx-font-size: 14px;");
        usersComboBox.setPrefHeight(38);
        usersComboBox.setMaxWidth(Double.MAX_VALUE);

        cashierBox.getChildren().addAll(cashierLabel, usersComboBox);

        if (st != null) {
            loadUsers();
        }

        // Buttons
        VBox buttonsBox = new VBox(12);

        Button generateBtn = ThemeUI.createButton("Generate Report");
        generateBtn.setMaxWidth(Double.MAX_VALUE);
        generateBtn.setPrefHeight(42);
        generateBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        generateBtn.setOnAction(e -> generateReport());

        Button exportBtn = ThemeUI.createButton("Export to PDF");
        exportBtn.setMaxWidth(Double.MAX_VALUE);
        exportBtn.setPrefHeight(42);
        exportBtn.setStyle(ThemeUI.buttonPrimary() + "-fx-font-size: 14px;");
        exportBtn.setOnAction(e -> exportToPDF());

        buttonsBox.getChildren().addAll(generateBtn, exportBtn);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        periodComboBox.setUserData(dateRangeBox);

        panel.getChildren().addAll(
                title,
                periodBox,
                dateRangeBox,
                cashierBox,
                spacer,
                buttonsBox
        );

        return panel;
    }

    private VBox createPreviewPanel() {
        VBox panel = new VBox(15);
        panel.setStyle(ThemeUI.cardStyle());
        panel.setPadding(new Insets(18));

        Text title = new Text("Report Preview");
        title.setFill(Color.web(ThemeUI.TEXT_COLOR));
        title.setFont(ThemeUI.getFontBold());

        reportPreviewArea = new TextArea();
        reportPreviewArea.setEditable(false);
        reportPreviewArea.setWrapText(true);
        reportPreviewArea.setStyle(
                "-fx-background-color: " + ThemeUI.BG_COLOR + ";" +
                        "-fx-control-inner-background: " + ThemeUI.BG_COLOR + ";" +
                        "-fx-text-fill: " + ThemeUI.TEXT_COLOR + ";" +
                        "-fx-border-color: " + ThemeUI.BLACK_COLOR + ";" +
                        "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 12px;"
        );
        reportPreviewArea.setText("Click 'Generate Report' to view sales data and inventory status...");

        VBox.setVgrow(reportPreviewArea, Priority.ALWAYS);

        panel.getChildren().addAll(title, reportPreviewArea);

        return panel;
    }

    private void toggleDatePickers() {
        String period = periodComboBox.getValue();
        VBox dateRangeBox = (VBox) periodComboBox.getUserData();

        if (dateRangeBox != null) {
            boolean showCustom = "Custom Range".equals(period);
            dateRangeBox.setManaged(showCustom);
            dateRangeBox.setVisible(showCustom);
        }
    }

    private void loadUsers() {
        try {
            String query = "SELECT id, display_name FROM users ORDER BY display_name";
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("display_name");
                usersComboBox.getItems().add(id + " - " + name);
            }

            rs.close();
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateReport() {
        if (st == null) {
            reportPreviewArea.setText("Error: Database connection not available.");
            return;
        }

        try {
            StringBuilder report = new StringBuilder();
            report.append("========================================\n");
            report.append("       COFFEE SHOP SALES REPORT\n");
            report.append("========================================\n\n");

            DateRange dateRange = getDateRange();
            report.append("Period: ").append(dateRange.label).append("\n");
            report.append("From: ").append(dateRange.fromDate).append("\n");
            report.append("To: ").append(dateRange.toDate).append("\n\n");

            String cashierFilter = getUserFilter();
            report.append("User: ").append(cashierFilter).append("\n");
            report.append("========================================\n\n");

            report.append("SALES BY USER:\n");
            report.append("----------------------------------------\n");
            String salesByCashierReport = generateSalesByUserReport(dateRange);
            report.append(salesByCashierReport).append("\n");

            report.append("TOP SELLING PRODUCTS:\n");
            report.append("----------------------------------------\n");
            String topProductsReport = generateTopProductsReport(dateRange);
            report.append(topProductsReport).append("\n");

            report.append("INVENTORY STATUS:\n");
            report.append("----------------------------------------\n");
            String inventoryReport = generateInventoryReport();
            report.append(inventoryReport).append("\n");

            report.append("========================================\n");
            report.append("Report generated: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            reportPreviewArea.setText(report.toString());

        } catch (Exception e) {
            reportPreviewArea.setText("Error generating report:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateSalesByUserReport(DateRange dateRange) {
        StringBuilder report = new StringBuilder();

        try {
            String cashierCondition = getUserSQLFilter();
            String query = String.format(
                    "SELECT u.display_name, COUNT(o.id) as order_count, " +
                            "SUM(o.total_lbp) as total_sales " +
                            "FROM orders o " +
                            "JOIN users u ON o.cashier_id = u.id " +
                            "WHERE o.created_at >= '%s' AND o.created_at <= '%s' %s " +
                            "GROUP BY u.id, u.display_name " +
                            "ORDER BY total_sales DESC",
                    dateRange.fromDate, dateRange.toDate, cashierCondition
            );

            ResultSet rs = st.executeQuery(query);

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String name = rs.getString("display_name");
                int orderCount = rs.getInt("order_count");
                int totalSales = rs.getInt("total_sales");

                report.append(String.format("%-20s | Orders: %3d | Sales: %,d LBP\n",
                        name, orderCount, totalSales));
            }

            if (!hasData) {
                report.append("No sales data for this period.\n");
            }

            rs.close();

        } catch (Exception e) {
            report.append("Error: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        return report.toString();
    }

    private String generateTopProductsReport(DateRange dateRange) {
        StringBuilder report = new StringBuilder();

        try {
            String query = String.format(
                    "SELECT p.name, p.category, SUM(oi.quantity) as total_qty, " +
                            "SUM(oi.line_total_lbp) as total_revenue " +
                            "FROM order_items oi " +
                            "JOIN products p ON oi.product_id = p.id " +
                            "JOIN orders o ON oi.order_id = o.id " +
                            "WHERE o.created_at >= '%s' AND o.created_at <= '%s' " +
                            "AND p.is_addon = 0 " +
                            "GROUP BY p.id, p.name, p.category " +
                            "ORDER BY total_qty DESC " +
                            "LIMIT 10",
                    dateRange.fromDate, dateRange.toDate
            );

            ResultSet rs = st.executeQuery(query);

            boolean hasData = false;
            int rank = 1;
            while (rs.next()) {
                hasData = true;
                String name = rs.getString("name");
                String category = rs.getString("category");
                int totalQty = rs.getInt("total_qty");
                int totalRevenue = rs.getInt("total_revenue");

                report.append(String.format("%2d. %-25s | Qty: %3d | Revenue: %,d LBP\n",
                        rank++, name + " (" + category + ")", totalQty, totalRevenue));
            }

            if (!hasData) {
                report.append("No product sales for this period.\n");
            }

            rs.close();

        } catch (Exception e) {
            report.append("Error: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        return report.toString();
    }

    private String generateInventoryReport() {
        StringBuilder report = new StringBuilder();

        try {
            String query =
                    "SELECT name, category, stock_qty, min_stock_qty, " +
                            "CASE WHEN stock_qty <= min_stock_qty THEN 'LOW' ELSE 'OK' END as status " +
                            "FROM products " +
                            "WHERE is_active = 1 " +
                            "ORDER BY " +
                            "  CASE WHEN stock_qty <= min_stock_qty THEN 0 ELSE 1 END, " +
                            "  category, name";

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                String name = rs.getString("name");
                String category = rs.getString("category");
                int stockQty = rs.getInt("stock_qty");
                int minStock = rs.getInt("min_stock_qty");
                String status = rs.getString("status");

                String statusIcon = status.equals("LOW") ? "[!]" : "   ";

                report.append(String.format("%s %-25s | Stock: %3d | Min: %3d | %s\n",
                        statusIcon, name + " (" + category + ")", stockQty, minStock, status));
            }

            rs.close();

        } catch (Exception e) {
            report.append("Error: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        return report.toString();
    }

    private void exportToPDF() {
        if (st == null) {
            System.err.println("Database connection not available.");
            return;
        }

        try {
            DateRange dateRange = getDateRange();
            String cashierFilter = getUserFilter();

            ReportData data = new ReportData();
            data.periodLabel = dateRange.label;
            data.fromDate = dateRange.fromDate;
            data.toDate = dateRange.toDate;
            data.cashierFilter = cashierFilter;
            data.salesByCashier = collectSalesByUser(dateRange);
            data.topProducts = collectTopProducts(dateRange);
            data.inventoryItems = collectInventoryItems();

            String fileName = "Sales_Report_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            String filePath = System.getProperty("user.home") + "/Desktop/" + fileName;

            ReportsPDFThread pdfThread = new ReportsPDFThread(filePath, data);
            pdfThread.start();

        } catch (Exception e) {
            System.err.println("Error exporting to PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<SalesByCashierRow> collectSalesByUser(DateRange dateRange) {
        List<SalesByCashierRow> rows = new ArrayList<>();

        try {
            String cashierCondition = getUserSQLFilter();
            String query = String.format(
                    "SELECT u.display_name, COUNT(o.id) as order_count, " +
                            "SUM(o.total_lbp) as total_sales " +
                            "FROM orders o " +
                            "JOIN users u ON o.cashier_id = u.id " +
                            "WHERE o.created_at >= '%s' AND o.created_at <= '%s' %s " +
                            "GROUP BY u.id, u.display_name " +
                            "ORDER BY total_sales DESC",
                    dateRange.fromDate, dateRange.toDate, cashierCondition
            );

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                SalesByCashierRow row = new SalesByCashierRow();
                row.cashierName = rs.getString("display_name");
                row.orderCount = rs.getInt("order_count");
                row.totalSales = rs.getInt("total_sales");
                rows.add(row);
            }

            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    private List<TopProductRow> collectTopProducts(DateRange dateRange) {
        List<TopProductRow> rows = new ArrayList<>();

        try {
            String query = String.format(
                    "SELECT p.name, p.category, SUM(oi.quantity) as total_qty, " +
                            "SUM(oi.line_total_lbp) as total_revenue " +
                            "FROM order_items oi " +
                            "JOIN products p ON oi.product_id = p.id " +
                            "JOIN orders o ON oi.order_id = o.id " +
                            "WHERE o.created_at >= '%s' AND o.created_at <= '%s' " +
                            "AND p.is_addon = 0 " +
                            "GROUP BY p.id, p.name, p.category " +
                            "ORDER BY total_qty DESC " +
                            "LIMIT 10",
                    dateRange.fromDate, dateRange.toDate
            );

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                TopProductRow row = new TopProductRow();
                row.productName = rs.getString("name");
                row.category = rs.getString("category");
                row.totalQty = rs.getInt("total_qty");
                row.totalRevenue = rs.getInt("total_revenue");
                rows.add(row);
            }

            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    private List<InventoryRow> collectInventoryItems() {
        List<InventoryRow> rows = new ArrayList<>();

        try {
            String query =
                    "SELECT name, category, stock_qty, min_stock_qty, " +
                            "CASE WHEN stock_qty <= min_stock_qty THEN 'LOW' ELSE 'OK' END as status " +
                            "FROM products " +
                            "WHERE is_active = 1 " +
                            "ORDER BY " +
                            "  CASE WHEN stock_qty <= min_stock_qty THEN 0 ELSE 1 END, " +
                            "  category, name";

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                InventoryRow row = new InventoryRow();
                row.productName = rs.getString("name");
                row.category = rs.getString("category");
                row.stockQty = rs.getInt("stock_qty");
                row.minStock = rs.getInt("min_stock_qty");
                row.status = rs.getString("status");
                rows.add(row);
            }

            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    private DateRange getDateRange() {
        String period = periodComboBox.getValue();
        DateRange range = new DateRange();

        LocalDate now = LocalDate.now();

        switch (period) {
            case "Today":
                range.fromDate = now.toString() + " 00:00:00";
                range.toDate = now.toString() + " 23:59:59";
                range.label = "Today";
                break;

            case "This Week":
                LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
                range.fromDate = weekStart.toString() + " 00:00:00";
                range.toDate = now.toString() + " 23:59:59";
                range.label = "This Week";
                break;

            case "This Month":
                LocalDate monthStart = now.withDayOfMonth(1);
                range.fromDate = monthStart.toString() + " 00:00:00";
                range.toDate = now.toString() + " 23:59:59";
                range.label = "This Month";
                break;

            case "Custom Range":
                range.fromDate = fromDatePicker.getValue().toString() + " 00:00:00";
                range.toDate = toDatePicker.getValue().toString() + " 23:59:59";
                range.label = "Custom Range";
                break;

            default:
                range.fromDate = now.toString() + " 00:00:00";
                range.toDate = now.toString() + " 23:59:59";
                range.label = "Today";
        }

        return range;
    }

    private String getUserFilter() {
        String selected = usersComboBox.getValue();
        if (selected == null || selected.equals("All Users")) {
            return "All Users";
        }
        return selected;
    }

    private String getUserSQLFilter() {
        String selected = usersComboBox.getValue();
        if (selected == null || selected.equals("All Users")) {
            return "";
        }

        String[] parts = selected.split(" - ");
        if (parts.length > 0) {
            try {
                int userId = Integer.parseInt(parts[0]);
                return " AND o.cashier_id = " + userId;
            } catch (NumberFormatException e) {
                return "";
            }
        }
        return "";
    }

    public static class DateRange {
        public String fromDate;
        public String toDate;
        public String label;
    }

    public static class ReportData {
        public String periodLabel;
        public String fromDate;
        public String toDate;
        public String cashierFilter;
        public List<SalesByCashierRow> salesByCashier;
        public List<TopProductRow> topProducts;
        public List<InventoryRow> inventoryItems;
    }

    public static class SalesByCashierRow {
        public String cashierName;
        public int orderCount;
        public int totalSales;
    }

    public static class TopProductRow {
        public String productName;
        public String category;
        public int totalQty;
        public int totalRevenue;
    }

    public static class InventoryRow {
        public String productName;
        public String category;
        public int stockQty;
        public int minStock;
        public String status;
    }
}
