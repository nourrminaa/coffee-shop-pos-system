//package com.coffeeshop;
//
//import com.coffeeshop.db.DBConnectMySQL;
//import com.coffeeshop.ui.LoginView;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.layout.HBox;
//import javafx.stage.Stage;
//
//import java.sql.Statement;
//
//public class TestingMain2 extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        try {
//            DBConnectMySQL db = new DBConnectMySQL();
//            Statement stmt = db.getStatement();
//
////            TestPDFThread tester:
////            System.out.println("[MAIN] Starting PDF thread...");
////            TestPDFThread t = new TestPDFThread("test_receipt1.pdf");
////            t.start();
////            System.out.println("[MAIN] Thread started!");
//
//            LoginView loginView = new LoginView(stmt);
////
//            HBox root = loginView.getLoginGUI();
////
//            Scene scene = new Scene(root, 4000, 4000);
//
//            primaryStage.setScene(scene);
//            primaryStage.setTitle("CoffeeShop POS");
//            primaryStage.setFullScreen(true); // to launch the stage in full screen
//            primaryStage.show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
