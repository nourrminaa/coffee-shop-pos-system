package com.coffeeshop.utils;

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

// extends a thread so the pdf generation will happen in the background of the app without freezing the ui
public class ReportsPDFThread extends Thread {

    private String filePath;
    private ReportData data;

    public ReportsPDFThread(String filePath, ReportData data) {
        this.filePath = filePath;
        this.data = data;
    }

    // run function executes on .start() method call (in the ExportPDFHandler class)
    @Override
    public void run() {
        // for console debugging purposes only
        System.out.println("[Thread] Starting PDF creation for report...");

        // PDDocument creates an empty PDF
        try (PDDocument doc = new PDDocument()) {

            // A4 page size for detailed reports
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page); // add the page to the PDDocument

            // load our fonts
            PDType0Font regular = PDType0Font.load(doc, new File("src/main/resources/fonts/Montserrat-Regular.ttf"));
            PDType0Font bold = PDType0Font.load(doc, new File("src/main/resources/fonts/Montserrat-Bold.ttf"));

            // cs is considered as the 'pen' in which we're writing with the doc page
            PDPageContentStream cs = new PDPageContentStream(doc, page);

            // get page width/height
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            // set margin to 50
            float margin = 50;
            // set the starting coordinates based on margin
            float left = margin;
            float right = pageWidth - margin;
            float y = pageHeight - margin;

            // header
            // centerText() is a helper method to center the texts to the middle of the page
            centerText(cs, bold, 18, "COFFEE SHOP", page, y);
            y -= 20; // y is decreasing to move text down (endl)
            centerText(cs, regular, 12, "Sales & Inventory Report", page, y);
            y -= 15;
            centerText(cs, regular, 10, "Zahle, Bekaa, LB", page, y);
            y -= 30;
            // draws a horizontal line (helper function)
            drawSolidLine(cs, left, right, y);
            y -= 25;

            // report details
            // set font + where the line should start
            cs.beginText();
            cs.setFont(regular, 10);
            cs.newLineAtOffset(left, y);

            cs.showText("Report Period: " + data.periodLabel);
            cs.newLineAtOffset(0, -14);
            cs.showText("From: " + data.fromDate.split(" ")[0]);
            cs.newLineAtOffset(0, -14);
            cs.showText("To: " + data.toDate.split(" ")[0]);
            cs.newLineAtOffset(0, -14);
            cs.showText("Cashier Filter: " + data.cashierFilter);
            cs.newLineAtOffset(0, -14);
            cs.showText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            cs.endText();
            y -= 85;
            drawSolidLine(cs, left, right, y);
            y -= 25;

            // sales by user
            cs.beginText();
            cs.setFont(bold, 14);
            cs.newLineAtOffset(left, y);
            cs.showText("SALES BY USER");
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
                // table header
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

                // each row
                for (SalesByCashierRow row : data.salesByCashier) {
                    cs.beginText();
                    cs.setFont(regular, 9);
                    cs.newLineAtOffset(left, y);

                    cs.showText(row.cashierName);
                    cs.newLineAtOffset(200, 0);
                    cs.showText(String.valueOf(row.orderCount)); // since it needs to be a string
                    cs.newLineAtOffset(80, 0);
                    cs.showText(String.valueOf(row.totalSales));
                    cs.endText();
                    y -= 12;

                    // if too close to the bottom:
                    // - close current page
                    // - open a new page
                    // - reset y
                    if (y < 150) {
                        try {
                            cs.close();
                            page = new PDPage(PDRectangle.A4);
                            doc.addPage(page);
                            cs = new PDPageContentStream(doc, page);
                            y = pageHeight - margin;
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                }
            }

            y -= 15;
            drawSolidLine(cs, left, right, y);
            y -= 25;

            // top selling products
            // before starting, if we’re too low, move to a new page
            if (y < 200) {
                try {
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    y = pageHeight - margin;
                } catch (Exception ex) { ex.printStackTrace(); }
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
                // header
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

                int rank = 1;
                for (TopProductRow row : data.topProducts) {
                    cs.beginText();
                    cs.setFont(regular, 9);
                    cs.newLineAtOffset(left, y);

                    cs.showText(String.valueOf(rank++));
                    cs.newLineAtOffset(40, 0);
                    cs.showText(row.productName + " (" + row.category + ")");
                    cs.newLineAtOffset(180, 0);
                    cs.showText(String.valueOf(row.totalQty));
                    cs.newLineAtOffset(50, 0);
                    cs.showText(String.valueOf(row.totalRevenue));
                    cs.endText();
                    y -= 12;

                    if (y < 150) {
                        try {
                            cs.close();
                            page = new PDPage(PDRectangle.A4);
                            doc.addPage(page);
                            cs = new PDPageContentStream(doc, page);
                            y = pageHeight - margin;
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                }
            }

            y -= 15;
            drawSolidLine(cs, left, right, y);
            y -= 25;

            // inventory
            if (y < 200) {
                try {
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    y = pageHeight - margin;
                } catch (Exception ex) { ex.printStackTrace(); }
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
                // header
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

                for (InventoryRow row : data.inventoryItems) {
                    // if inventory status is low, make the font bold
                    PDType0Font rowFont = row.status.equals("LOW") ? bold : regular;

                    cs.beginText();
                    cs.setFont(rowFont, 9);
                    cs.newLineAtOffset(left, y);

                    String prefix = row.status.equals("LOW") ? "[!] " : "";
                    cs.showText(prefix + row.productName + " (" + row.category + ")");
                    cs.newLineAtOffset(200, 0);
                    cs.showText(String.valueOf(row.stockQty));
                    cs.newLineAtOffset(60, 0);
                    cs.showText(String.valueOf(row.minStock));
                    cs.newLineAtOffset(60, 0);
                    cs.showText(row.status);
                    cs.endText();
                    y -= 12;

                    if (y < 100) {
                        try {
                            cs.close();
                            page = new PDPage(PDRectangle.A4);
                            doc.addPage(page);
                            cs = new PDPageContentStream(doc, page);
                            y = pageHeight - margin;
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                }
            }

            y -= 15;
            drawSolidLine(cs, left, right, y);
            y -= 30;

            // footer
            centerText(cs, regular, 9, "This report is system-generated and confidential.", page, y);
            y -= 12;
            centerText(cs, regular, 9, "Coffee Shop POS System", page, y);

            cs.close();
            doc.save(filePath);
            System.out.println("[Thread] PDF report created at → " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            // create an info alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText("Report Exported");
            alert.setContentText("PDF report saved at:\n" + filePath);
            alert.setGraphic(null); // no icon graphic (the '?' mark)
            // include our custom alert
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());
            // connecting the CSS classes from alert.css to the JavaFX UI nodes inside the Alert
            alert.getDialogPane().getStyleClass().add("custom-alert-pane");
            alert.getDialogPane().lookup(".header-panel").getStyleClass().add("custom-alert-header");
            alert.getDialogPane().lookup(".content").getStyleClass().add("custom-alert-content");
            alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0)).getStyleClass().add("custom-button");

            alert.show();
        });
    }

    // helper methods
    private void centerText(PDPageContentStream cs, PDType0Font font, int size, String text, PDPage page, float y){
        try {
            float textWidth = font.getStringWidth(text) / 1000 * size;
            float pageWidth = page.getMediaBox().getWidth();
            float centerX = (pageWidth - textWidth) / 2;

            cs.beginText();
            cs.setFont(font, size);
            cs.newLineAtOffset(centerX, y);
            cs.showText(text);
            cs.endText();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void drawSolidLine(PDPageContentStream cs, float left, float right, float y) {
        try {
            cs.setLineWidth(1f);
            cs.moveTo(left, y);
            cs.lineTo(right, y);
            cs.stroke();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void drawDashedLine(PDPageContentStream cs, float left, float right, float y) {
        try {
            cs.setLineWidth(0.5f);
            cs.setLineDashPattern(new float[]{3, 3}, 0);
            cs.moveTo(left, y);
            cs.lineTo(right, y);
            cs.stroke();
            cs.setLineDashPattern(new float[]{}, 0);
        } catch (Exception e) { e.printStackTrace(); }
    }
}