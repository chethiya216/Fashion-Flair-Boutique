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
   // 1. Updated return type from List<Product> to List<PCatalog>
    public List<PCatalog> searchCatalog(String barcode, String productName, String categoryFilter) {
        List<PCatalog> products = new ArrayList<>();

        // Base query joining products and categories
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.category_name " +
            "FROM products p " +
            "JOIN categories c ON p.category_id = c.category_id " +
            "WHERE p.status = 'ACTIVE'"
        );

        // Check which search inputs are active
        boolean hasBarcode = barcode != null && !barcode.trim().isEmpty();
        boolean hasPName = productName != null && !productName.trim().isEmpty();
        boolean hasCategoryFilter = categoryFilter != null 
                && !categoryFilter.trim().isEmpty() 
                && !categoryFilter.equalsIgnoreCase("All Categories");

        // Build dynamic WHERE conditions
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

            // Bind variables based on active conditions
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
                    pc.setProductId(rs.getInt("product_id"));
                    pc.setBarcode(rs.getString("barcode"));
                    pc.setProductName(rs.getString("product_name"));
                    pc.setCategoryId(rs.getInt("category_id"));
                    pc.setCategoryName(rs.getString("category_name"));
                    pc.setBrand(rs.getString("brand"));

                    // Added missing fields (matches JTable columns)
                    pc.setTargetGroup(rs.getString("target_group"));
                    pc.setSize(rs.getString("size"));
                    pc.setColor(rs.getString("color"));

                    // Populate selling price
                    pc.setSellingPrice(rs.getDouble("selling_price"));

                    // Safely parse discount percentage
                    double discount = rs.getDouble("discount_percentage");
                    pc.setDiscountPercentage(rs.wasNull() ? 0.0 : discount);

                    pc.setStockQuantity(rs.getInt("stock_quantity"));
                    pc.setStatus(rs.getString("status"));

                    products.add(pc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
}
