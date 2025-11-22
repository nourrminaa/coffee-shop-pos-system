package com.coffeeshop.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;

public class TestPDFThread extends Thread {

    private final String filePath;

    public TestPDFThread(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void run() {
        System.out.println("[Thread] Starting PDF creation...");

        try (PDDocument doc = new PDDocument()) {

            // Narrow page to mimic real receipts
            PDPage page = new PDPage(new PDRectangle(250, 700));
            doc.addPage(page);

            PDType0Font regular = PDType0Font.load(doc,
                    new File("src/main/resources/fonts/Montserrat-Regular.ttf"));
            PDType0Font bold = PDType0Font.load(doc,
                    new File("src/main/resources/fonts/Montserrat-Bold.ttf"));

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            float left = 20;
            float right = 220;
            float y = 660;

            // ---------------------- SHOP HEADER ----------------------
            centerText(cs, bold, 14, "COFFEE SHOP", page, y);
            y -= 20; // Each time we reduce y -> move downward
            centerText(cs, regular, 10, "Zahle, Bekaa, LB", page, y);
            y -= 15;
            centerText(cs, regular, 10, "Tel: +961 00 000 000", page, y);
            y -= 25;

            drawDashedSeparator(cs, left, right, y);
            y -= 20;

            // ---------------------- ORDER DETAILS ----------------------
            cs.beginText();
            cs.setFont(regular, 10);
            cs.newLineAtOffset(left, y);
            cs.showText("Order ID: 1");
            cs.newLineAtOffset(0, -12);
            cs.showText("Cashier: nour");
            cs.newLineAtOffset(0, -12);
            cs.showText("Date: 21/11/2025");
            cs.newLineAtOffset(0, -12);
            cs.showText("Time: 6:45 PM");
            cs.endText();
            y -= 60;

            drawDashedSeparator(cs, left, right, y);
            y -= 20;

            // ---------------------- ITEMS ----------------------
            cs.beginText();
            cs.setFont(bold, 11);
            cs.newLineAtOffset(left, y);
            cs.showText("ITEMS");
            cs.endText();
            y -= 18;

            drawItemLine(cs, regular, left, right, y, "Espresso x2", "40,000");
            y -= 14;
            drawItemLine(cs, regular, left, right, y, "Croissant x1", "10,000");
            y -= 30;

            drawDashedSeparator(cs, left, right, y);
            y -= 20;

            // ---------------------- TOTAL ----------------------
            cs.beginText();
            cs.setFont(bold, 12);
            cs.newLineAtOffset(left, y);
            cs.showText("TOTAL");
            cs.endText();

            cs.beginText();
            cs.setFont(bold, 12);
            cs.newLineAtOffset(right - 60, y);
            cs.showText("50,000 LBP");
            cs.endText();
            y -= 25;

            drawDashedSeparator(cs, left, right, y);
            y -= 20;

            // ---------------------- FOOTER ----------------------
            centerText(cs, regular, 10, "Thank you for your purchase!", page, y);
            y -= 15;
            centerText(cs, regular, 10, "Visit Again!", page, y);

            cs.close();
            doc.save(filePath);
            System.out.println("[Thread] PDF created at → " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText("Receipt Created");
            alert.setContentText("Saved at:\n" + filePath);

            alert.setGraphic(null); // remove default icon

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

    // ---------- Center text ----------
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

    // ---------- Dotted/Dashed separator ----------
    private void drawDashedSeparator(PDPageContentStream cs, float left, float right, float y) throws Exception {
        cs.setLineWidth(0.5f);
        cs.setLineDashPattern(new float[]{3, 3}, 0);
        cs.moveTo(left, y);
        cs.lineTo(right, y);
        cs.stroke();
        cs.setLineDashPattern(new float[]{}, 0); // reset
    }

    // ---------- Right-aligned price line ----------
    private void drawItemLine(PDPageContentStream cs, PDType0Font font,
                              float left, float right, float y,
                              String item, String price) throws Exception {

        cs.beginText();
        cs.setFont(font, 10);
        cs.newLineAtOffset(left, y);
        cs.showText(item);
        cs.endText();

        float priceWidth = font.getStringWidth(price) / 1000 * 10;

        cs.beginText();
        cs.setFont(font, 10);
        cs.newLineAtOffset(right - priceWidth, y);
        cs.showText(price);
        cs.endText();
    }
}
