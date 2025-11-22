package com.coffeeshop.db;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class DBConnectMySQL {
    private Statement stmt;

    public DBConnectMySQL(){
//        because IntelliJ runs JavaFX apps from weird locations,
//        dotenv often cannot find .env unless you tell it exactly where it is
//        this line is like saying: “load the .env file from EXACTLY this directory.”
        Dotenv dotenv = Dotenv.configure().directory("/Users/user/Desktop/oopii-project").load();

        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try{
            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
            Connection con = DriverManager.getConnection(url, user, password);

            this.stmt = con.createStatement();
            System.out.println("Connected to MySQL database!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to MySQL database :(");
            e.printStackTrace();
        }
    }

    public Statement getStatement() {
        return this.stmt;
    }
}
