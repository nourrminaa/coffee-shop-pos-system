package com.coffeeshop.utils;

import com.coffeeshop.ui.ReportsView.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportsPDFThread extends Thread {

    private final String filePath;
    private final ReportData data;

    public ReportsPDFThread(String filePath, ReportData data) {
        this.filePath = filePath;
        this.data = data;
    }

    @Override
    public void run() {
        System.out.println("[Thread] Starting PDF creation for report...");

        try (PDDocument doc = new PDDocument()) {

            // A4 page size for detailed reports
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType0Font regular = PDType0Font.load(doc,
                    new File("src/main/resources/fonts/Montserrat-Regular.ttf"));
            PDType0Font bold = PDType0Font.load(doc,
                    new File("src/main/resources/fonts/Montserrat-Bold.ttf"));

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float margin = 50;
            float left = margin;
            float right = pageWidth - margin;
            float y = pageHeight - margin;

            // ---------------------- HEADER ----------------------
            centerText(cs, bold, 18, "COFFEE SHOP", page, y);
            y -= 20;
            centerText(cs, regular, 12, "Sales & Inventory Report", page, y);
            y -= 15;
            centerText(cs, regular, 10, "Zahle, Bekaa, LB", page, y);
            y -= 30;

            drawSolidLine(cs, left, right, y);
            y -= 25;

            // ---------------------- REPORT DETAILS ----------------------
            cs.beginText();
            cs.setFont(regular, 10);
            cs.newLineAtOffset(left, y);
            cs.showText("Report Period: " + data.periodLabel);
            cs.newLineAtOffset(0, -14);
            cs.showText("From: " + formatDate(data.fromDate));
            cs.newLineAtOffset(0, -14);
            cs.showText("To: " + formatDate(data.toDate));
            cs.newLineAtOffset(0, -14);
            cs.showText("Cashier Filter: " + data.cashierFilter);
            cs.newLineAtOffset(0, -14);
            cs.showText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            cs.endText();
            y -= 85;

            drawSolidLine(cs, left, right, y);
            y -= 25;

            // ---------------------- SALES BY CASHIER ----------------------
            cs.beginText();
            cs.setFont(bold, 14);
            cs.newLineAtOffset(left, y);
            cs.showText("SALES BY CASHIER");
            cs.endText();
            y -= 20;

            if (data.salesByCashier.isEmpty()) {
                cs.beginText();
                cs.setFont(regular, 10);
                cs.newLineAtOffset(left, y);
                cs.showText("No sales data for this period.");
                cs.endText();
                y -= 20;
            } else {
                // Table header
                cs.beginText();
                cs.setFont(bold, 10);
                cs.newLineAtOffset(left, y);
                cs.showText("Cashier");
                cs.newLineAtOffset(200, 0);
                cs.showText("Orders");
                cs.newLineAtOffset(80, 0);
                cs.showText("Total Sales (LBP)");
                cs.endText();
                y -= 15;

                drawDashedLine(cs, left, right, y);
                y -= 12;

                // Table rows
                for (SalesByCashierRow row : data.salesByCashier) {
                    cs.beginText();
                    cs.setFont(regular, 9);
                    cs.newLineAtOffset(left, y);
                    cs.showText(truncate(row.cashierName, 30));
                    cs.newLineAtOffset(200, 0);
                    cs.showText(String.valueOf(row.orderCount));
                    cs.newLineAtOffset(80, 0);
                    cs.showText(formatNumber(row.totalSales));
                    cs.endText();
                    y -= 12;

                    if (y < 150) {
                        cs.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        cs = new PDPageContentStream(doc, page);
                        y = pageHeight - margin;
                    }
                }
            }

            y -= 15;
            drawSolidLine(cs, left, right, y);
            y -= 25;

            // ---------------------- TOP SELLING PRODUCTS ----------------------
            if (y < 200) {
                cs.close();
                page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                cs = new PDPageContentStream(doc, page);
                y = pageHeight - margin;
            }

            cs.beginText();
            cs.setFont(bold, 14);
            cs.newLineAtOffset(left, y);
            cs.showText("TOP SELLING PRODUCTS");
            cs.endText();
            y -= 20;

            if (data.topProducts.isEmpty()) {
                cs.beginText();
                cs.setFont(regular, 10);
                cs.newLineAtOffset(left, y);
                cs.showText("No product sales for this period.");
                cs.endText();
                y -= 20;
            } else {
                // Table header
                cs.beginText();
                cs.setFont(bold, 10);
                cs.newLineAtOffset(left, y);
                cs.showText("Rank");
                cs.newLineAtOffset(40, 0);
                cs.showText("Product");
                cs.newLineAtOffset(180, 0);
                cs.showText("Qty");
                cs.newLineAtOffset(50, 0);
                cs.showText("Revenue (LBP)");
                cs.endText();
                y -= 15;

                drawDashedLine(cs, left, right, y);
                y -= 12;

                // Table rows
                int rank = 1;
                for (TopProductRow row : data.topProducts) {
                    cs.beginText();
                    cs.setFont(regular, 9);
                    cs.newLineAtOffset(left, y);
                    cs.showText(String.valueOf(rank++));
                    cs.newLineAtOffset(40, 0);
                    cs.showText(truncate(row.productName + " (" + row.category + ")", 28));
                    cs.newLineAtOffset(180, 0);
                    cs.showText(String.valueOf(row.totalQty));
                    cs.newLineAtOffset(50, 0);
                    cs.showText(formatNumber(row.totalRevenue));
                    cs.endText();
                    y -= 12;

                    if (y < 150) {
                        cs.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        cs = new PDPageContentStream(doc, page);
                        y = pageHeight - margin;
                    }
                }
            }

            y -= 15;
            drawSolidLine(cs, left, right, y);
            y -= 25;

            // ---------------------- INVENTORY STATUS ----------------------
            if (y < 200) {
                cs.close();
                page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                cs = new PDPageContentStream(doc, page);
                y = pageHeight - margin;
            }

            cs.beginText();
            cs.setFont(bold, 14);
            cs.newLineAtOffset(left, y);
            cs.showText("INVENTORY STATUS");
            cs.endText();
            y -= 20;

            if (data.inventoryItems.isEmpty()) {
                cs.beginText();
                cs.setFont(regular, 10);
                cs.newLineAtOffset(left, y);
                cs.showText("No inventory data available.");
                cs.endText();
                y -= 20;
            } else {
                // Table header
                cs.beginText();
                cs.setFont(bold, 10);
                cs.newLineAtOffset(left, y);
                cs.showText("Product");
                cs.newLineAtOffset(200, 0);
                cs.showText("Stock");
                cs.newLineAtOffset(60, 0);
                cs.showText("Min");
                cs.newLineAtOffset(60, 0);
                cs.showText("Status");
                cs.endText();
                y -= 15;

                drawDashedLine(cs, left, right, y);
                y -= 12;

                // Table rows
                for (InventoryRow row : data.inventoryItems) {
                    PDType0Font rowFont = row.status.equals("LOW") ? bold : regular;

                    cs.beginText();
                    cs.setFont(rowFont, 9);
                    cs.newLineAtOffset(left, y);

                    String prefix = row.status.equals("LOW") ? "[!] " : "";
                    cs.showText(prefix + truncate(row.productName + " (" + row.category + ")", 26));
                    cs.newLineAtOffset(200, 0);
                    cs.showText(String.valueOf(row.stockQty));
                    cs.newLineAtOffset(60, 0);
                    cs.showText(String.valueOf(row.minStock));
                    cs.newLineAtOffset(60, 0);
                    cs.showText(row.status);
                    cs.endText();
                    y -= 12;

                    if (y < 100) {
                        cs.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        cs = new PDPageContentStream(doc, page);
                        y = pageHeight - margin;
                    }
                }
            }

            y -= 15;
            drawSolidLine(cs, left, right, y);
            y -= 30;

            // ---------------------- FOOTER ----------------------
            centerText(cs, regular, 9, "This report is system-generated and confidential.", page, y);
            y -= 12;
            centerText(cs, regular, 9, "Coffee Shop Management System", page, y);

            cs.close();
            doc.save(filePath);
            System.out.println("[Thread] PDF report created at → " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText("Report Exported");
            alert.setContentText("PDF report saved at:\n" + filePath);

            alert.setGraphic(null);

            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/styles/alert.css").toExternalForm()
            );

            alert.getDialogPane().getStyleClass().add("custom-alert-pane");
            alert.getDialogPane().lookup(".header-panel")
                    .getStyleClass().add("custom-alert-header");
            alert.getDialogPane().lookup(".content")
                    .getStyleClass().add("custom-alert-content");
            alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0))
                    .getStyleClass().add("custom-button");

            alert.show();
        });
    }

    // Helper methods
    private void centerText(PDPageContentStream cs, PDType0Font font, int size,
                            String text, PDPage page, float y) throws Exception {
        float textWidth = font.getStringWidth(text) / 1000 * size;
        float pageWidth = page.getMediaBox().getWidth();
        float centerX = (pageWidth - textWidth) / 2;

        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(centerX, y);
        cs.showText(text);
        cs.endText();
    }

    private void drawSolidLine(PDPageContentStream cs, float left, float right, float y) throws Exception {
        cs.setLineWidth(1f);
        cs.moveTo(left, y);
        cs.lineTo(right, y);
        cs.stroke();
    }

    private void drawDashedLine(PDPageContentStream cs, float left, float right, float y) throws Exception {
        cs.setLineWidth(0.5f);
        cs.setLineDashPattern(new float[]{3, 3}, 0);
        cs.moveTo(left, y);
        cs.lineTo(right, y);
        cs.stroke();
        cs.setLineDashPattern(new float[]{}, 0);
    }

    private String formatNumber(int number) {
        return String.format("%,d", number);
    }

    private String formatDate(String dateTimeStr) {
        // Convert "2025-11-24 00:00:00" to "24/11/2025"
        try {
            String[] parts = dateTimeStr.split(" ")[0].split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return dateTimeStr;
        }
    }

    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}