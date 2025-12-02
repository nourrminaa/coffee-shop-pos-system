package com.coffeeshop.utils;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;

public class CategoryUtils {

    public static String[] loadCategories(Statement st) {
        ArrayList<String> list = new ArrayList<>();

        try {
            String sql = "SELECT DISTINCT category FROM products WHERE is_active = 1 ORDER BY category";
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                list.add(rs.getString("category"));
            }
            rs.close();
        } catch (Exception e) {
            e.getMessage();
        }

        // +1 for the 'All' category
        String[] categories = new String[list.size() + 1];
        int idx = 0;

        // add the first category as 'All' products
        categories[idx++] = "All";
        for (String cat : list)
            categories[idx++] = cat;

        return categories;
    }
}
