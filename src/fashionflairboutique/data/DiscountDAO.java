/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/**
 *
 * @author Chethiya
 */
public class DiscountDAO {
    public List<Object[]> getAllPromotions() {
        List<Object[]> promos = new ArrayList<>();
        String sql = "SELECT p.promotion_id, p.promotion_name, p.description, p.discount_percentage, " +
                     "p.start_date, p.end_date, p.status, u.username AS creator_name " +
                     "FROM Promotions p " +
                     "LEFT JOIN Users u ON p.created_by = u.user_id";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                promos.add(new Object[]{
                    rs.getInt("promotion_id"),
                    rs.getString("promotion_name"),
                    rs.getString("description"),      // new - inserted here
                    rs.getDouble("discount_percentage"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("status"),
                    rs.getString("creator_name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promos;
    }
    
    public boolean createPromotion(String name, String description, double discountPercentage, String startDate, String endDate, String status, int createdBy) {
        String sql = "INSERT INTO promotions (promotion_name, description, discount_percentage, start_date, end_date, status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, discountPercentage);
            pstmt.setString(4, startDate);
            pstmt.setString(5, endDate);
            pstmt.setString(6, status);
            pstmt.setInt(7, createdBy);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean createPromotionByCategory(String name, String description, double discountPercentage, String startDate, String endDate, String status, String categoryName, int createdBy) {
        String insertPromoSQL = "INSERT INTO promotions (promotion_name, description, discount_percentage, start_date, end_date, status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String bulkLinkSQL = "INSERT INTO promotion_products (promotion_id, product_id) SELECT ?, p.product_id FROM products p JOIN categories c ON p.category_id = c.category_id WHERE c.category_name = ?";

        try (Connection conn = DatabaseConnector.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement promoStmt = conn.prepareStatement(insertPromoSQL, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement linkStmt = conn.prepareStatement(bulkLinkSQL)) {

                // 1. Insert Promotion
                promoStmt.setString(1, name);
                promoStmt.setString(2, description);
                promoStmt.setDouble(3, discountPercentage);
                promoStmt.setString(4, startDate);
                promoStmt.setString(5, endDate);
                promoStmt.setString(6, status);
                promoStmt.setInt(7, createdBy);

                int affectedRows = promoStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }

                // 2. Get the generated promotion_id
                try (ResultSet generatedKeys = promoStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int promotionId = generatedKeys.getInt(1);

                        // 3. Bulk Link Products matching the category name
                        linkStmt.setInt(1, promotionId);
                        linkStmt.setString(2, categoryName);
                        linkStmt.executeUpdate();
                    } else {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit(); // Commit transaction successfully
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Rollback if any query fails
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePromotionStatus(int promotionId, String status) {
        String sql = "UPDATE promotions SET status = ? WHERE promotion_id = ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, promotionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category_name FROM categories"; // Adjust table/column if needed
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    public List<String> getAllTargetGroups() {
        List<String> targetGroups = new ArrayList<>();
        // If target group is a column inside products or categories table:
        String sql = "SELECT DISTINCT target_group FROM products WHERE target_group IS NOT NULL"; 
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                targetGroups.add(rs.getString("target_group"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return targetGroups;
    }
    
    public int linkCategoryToPromotion(int promotionId, String categoryName, String targetGroup) throws IllegalStateException {
        String checkPromoSQL = "SELECT discount_percentage, status FROM Promotions WHERE promotion_id = ?";
        String linkSQL = "INSERT IGNORE INTO Promotion_Products (promotion_id, product_id) " +
                          "SELECT ?, p.product_id FROM products p " +
                          "JOIN categories c ON p.category_id = c.category_id " +
                          "WHERE c.category_name = ? AND p.target_group = ?";
        String syncDiscountSQL = "UPDATE products p JOIN categories c ON p.category_id = c.category_id " +
                                  "SET p.discount_percentage = ? " +
                                  "WHERE c.category_name = ? AND p.target_group = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnector.getInstance().getConnection();
            conn.setAutoCommit(false);

            double promoDiscount = 0.0;
            String status = null;
            boolean promotionExists = false;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkPromoSQL)) {
                checkStmt.setInt(1, promotionId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        promotionExists = true;
                        promoDiscount = rs.getDouble("discount_percentage");
                        status = rs.getString("status");
                    }
                }
            }

            if (!promotionExists) {
                conn.rollback();
                throw new IllegalStateException("Promotion not found.");
            }

            if (!"Active".equalsIgnoreCase(status)) {
                conn.rollback();
                throw new IllegalStateException("Only Active promotions can be linked to products. This promotion is currently: " + status);
            }

            int linkedCount;
            try (PreparedStatement linkStmt = conn.prepareStatement(linkSQL)) {
                linkStmt.setInt(1, promotionId);
                linkStmt.setString(2, categoryName);
                linkStmt.setString(3, targetGroup);
                linkedCount = linkStmt.executeUpdate();
            }

            if (linkedCount > 0) {
                try (PreparedStatement syncStmt = conn.prepareStatement(syncDiscountSQL)) {
                    syncStmt.setDouble(1, promoDiscount);
                    syncStmt.setString(2, categoryName);
                    syncStmt.setString(3, targetGroup);
                    syncStmt.executeUpdate();
                }
            }

            conn.commit();
            return linkedCount;

        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            return 0;
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }
    
    public List<Object[]> getCategoryLinksForPromotion(int promotionId) {
        List<Object[]> links = new ArrayList<>();
        String sql = "SELECT DISTINCT c.category_name, p.target_group, pr.promotion_name " +
                     "FROM Promotion_Products pp " +
                     "JOIN products p ON pp.product_id = p.product_id " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "JOIN Promotions pr ON pp.promotion_id = pr.promotion_id " +
                     "WHERE pp.promotion_id = ?";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, promotionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    links.add(new Object[]{
                        rs.getString("category_name"),
                        rs.getString("target_group"),
                        rs.getString("promotion_name")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return links;
    }
    
    public int unlinkCategoryFromPromotion(int promotionId, String categoryName, String targetGroup) {
        String sql = "DELETE pp FROM Promotion_Products pp " +
                     "JOIN products p ON pp.product_id = p.product_id " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "WHERE pp.promotion_id = ? AND c.category_name = ? AND p.target_group = ?";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, promotionId);
            stmt.setString(2, categoryName);
            stmt.setString(3, targetGroup);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public boolean updatePromotion(int promotionId, String name, String description, double discountPercentage,String startDate, String endDate, String status) {
        String sql = "UPDATE Promotions SET promotion_name = ?, description = ?, discount_percentage = ?, " +
                     "start_date = ?, end_date = ?, status = ? WHERE promotion_id = ?";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, discountPercentage);
            pstmt.setString(4, startDate);
            pstmt.setString(5, endDate);
            pstmt.setString(6, status);
            pstmt.setInt(7, promotionId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
