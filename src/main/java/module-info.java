module com.coffeeshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.pdfbox;
    requires io.github.cdimascio.dotenv.java;

    opens com.coffeeshop to javafx.fxml;
    exports com.coffeeshop;
    exports com.coffeeshop.ui;
    exports com.coffeeshop.utils;
}