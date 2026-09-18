/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import fashionflairboutique.models.Category;
import fashionflairboutique.models.PCatalog;
import fashionflairboutique.models.Product;
import fashionflairboutique.models.Product;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chethiya
 */
public class ProductCatalogDAO {
   public List<PCatalog> searchCatalog(String barcode, String productName, String categoryFilter) {
        List<PCatalog> products = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.category_name, " +
            "  COALESCE(" +
            "    (SELECT MAX(pr.discount_percentage) " +
            "     FROM Promotion_Products pp " +
            "     JOIN Promotions pr ON pp.promotion_id = pr.promotion_id " +
            "     WHERE pp.product_id = p.product_id " +
            "       AND pr.status = 'Active' " +
            "       AND CURDATE() BETWEEN pr.start_date AND pr.end_date), " +
            "    p.discount_percentage, 0" +
            "  ) AS effective_discount " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +
            "WHERE LOWER(p.status) = 'active'" // Case-insensitive status check
        );

        boolean hasBarcode = barcode != null && !barcode.trim().isEmpty();
        boolean hasPName = productName != null && !productName.trim().isEmpty();
        boolean hasCategoryFilter = categoryFilter != null
                && !categoryFilter.trim().isEmpty()
                && !categoryFilter.equalsIgnoreCase("All Categories");

        if (hasBarcode) {
            sql.append(" AND p.barcode LIKE ?");
        }
        if (hasPName) {
            sql.append(" AND p.product_name LIKE ?");
        }
        if (hasCategoryFilter) {
            sql.append(" AND c.category_name = ?");
        }

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasBarcode) {
                ps.setString(paramIndex++, "%" + barcode.trim() + "%");
            }
            if (hasPName) {
                ps.setString(paramIndex++, "%" + productName.trim() + "%");
            }
            if (hasCategoryFilter) {
                ps.setString(paramIndex++, categoryFilter.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PCatalog pc = new PCatalog();
                    
                    // 1. Set Primary Key
                    pc.setProductId(rs.getInt("product_id"));
                    
                    // 2. Set Basic Info
                    pc.setBarcode(rs.getString("barcode"));
                    pc.setProductName(rs.getString("product_name"));
                    pc.setCategoryId(rs.getInt("category_id"));
                    pc.setCategoryName(rs.getString("category_name") != null ? rs.getString("category_name") : "Uncategorized");
                    pc.setBrand(rs.getString("brand"));

                    // 3. Set Missing Variations (Target Group, Size, Color)
                    try {
                        pc.setTargetGroup(rs.getString("target_group"));
                        pc.setSize(rs.getString("size"));
                        pc.setColor(rs.getString("color"));
                    } catch (SQLException ignored) {
                        // In case column names differ in database schema
                    }

                    // 4. Calculate Selling & Discounted Prices
                    double sellingPrice = rs.getDouble("selling_price");
                    pc.setSellingPrice(sellingPrice);

                    double discount = rs.getDouble("effective_discount");
                    if (rs.wasNull()) {
                        discount = 0.0;
                    }
                    pc.setDiscountPercentage(discount);

                    // Calculate Final Discounted Price: Price * (1 - Discount / 100)
                    double discountedPrice = sellingPrice - (sellingPrice * (discount / 100.0));
                    pc.setDiscountedPrice(discountedPrice);

                    // 5. Stock & Status
                    pc.setStockQuantity(rs.getInt("stock_quantity"));
                    pc.setStatus(rs.getString("status"));

                    products.add(pc);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error in searchCatalog:");
            e.printStackTrace();
        }

        return products;
    }
}
