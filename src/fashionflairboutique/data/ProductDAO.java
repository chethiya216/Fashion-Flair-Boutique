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
import fashionflairboutique.models.Product;
import fashionflairboutique.data.DatabaseConnector;
/**
 *
 * @author Chethiya
 */
public class ProductDAO {
     // 1. Fetch Categories for Dropdown
    public List<Category> getAllCategories() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, category_name, description, status FROM categories " +
                 "WHERE status = 'Active' ORDER BY category_name ASC";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("description"),
                    rs.getString("status")
                ));
            }
        }
        return list;
    }

    // 2. Add New Product
    public boolean addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO products (barcode, product_name, brand, category_id, target_group, " +
             "size, color, buying_price, selling_price, discount_percentage, stock_quantity, status) " +
             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getBarcode());
            stmt.setString(2, p.getProductName());
            stmt.setString(3, p.getBrand());
            stmt.setInt(4, p.getCategoryId());
            stmt.setString(5, p.getTargetGroup());
            stmt.setString(6, p.getSize());
            stmt.setString(7, p.getColor());
            stmt.setBigDecimal(8, p.getBuyingPrice());
            stmt.setBigDecimal(9, p.getSellingPrice());
            stmt.setBigDecimal(10, p.getDiscountPercentage());
            stmt.setInt(11, p.getStockQuantity());
            stmt.setString(12, p.getStatus());

            return stmt.executeUpdate() > 0;
        }
    }

    // 3. Update Product
    public boolean updateProduct(Product p) throws SQLException {
        String sql = "UPDATE products SET barcode=?, product_name=?, brand=?, category_id=?, target_group=?, " +
                     "size=?, color=?, buying_price=?, selling_price=?, discount_percentage=?, stock_quantity=?, status=? " +
                     "WHERE product_id=?";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getBarcode());
            stmt.setString(2, p.getProductName());
            stmt.setString(3, p.getBrand());
            stmt.setInt(4, p.getCategoryId());
            stmt.setString(5, p.getTargetGroup());
            stmt.setString(6, p.getSize());
            stmt.setString(7, p.getColor());
            stmt.setBigDecimal(8, p.getBuyingPrice());
            stmt.setBigDecimal(9, p.getSellingPrice());
            stmt.setBigDecimal(10, p.getDiscountPercentage());
            stmt.setInt(11, p.getStockQuantity());
            stmt.setString(12, p.getStatus());
            stmt.setInt(13, p.getProductId());

            return stmt.executeUpdate() > 0;
        }
    }

    // 4. Delete Product
    public boolean deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    // 5. Combined Search (By Category, Target Group, or Keyword) - used by ManageProducts.
    // Reads the PLAIN stored discount_percentage - this screen edits that value
    // directly, so it must always show/save the real default, never a promo-
    // inflated figure. Do not apply the effective-discount fix here.
    public List<Product> searchProducts(int categoryId, String targetGroup, String keyword) throws SQLException {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.category_name FROM products p " +
            "JOIN categories c ON p.category_id = c.category_id WHERE 1=1"
        );

        if (categoryId > 0) {
            sql.append(" AND p.category_id = ?");
        }
        if (targetGroup != null && !targetGroup.equalsIgnoreCase("All")) {
            sql.append(" AND p.target_group = ?");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (p.product_name LIKE ? OR p.brand LIKE ? OR p.barcode LIKE ?)");
        }
        sql.append(" ORDER BY p.product_id DESC");

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (categoryId > 0) {
                stmt.setInt(paramIndex++, categoryId);
            }
            if (targetGroup != null && !targetGroup.equalsIgnoreCase("All")) {
                stmt.setString(paramIndex++, targetGroup);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        }
        return list;
    }

    // 6. NEW: Search by category name for the SearchProducts (browse-only) screen.
    // Reads effective_discount - promo-aware, since this screen is for browsing
    // what a customer would actually pay right now, not editing the stored default.
    public List<Product> getProductsByCategory(String category) throws SQLException {
        List<Product> productList = new ArrayList<>();
        String sql =
            "SELECT p.*, c.category_name, " +
            "  COALESCE(" +
            "    (SELECT MAX(pr.discount_percentage) " +
            "     FROM Promotion_Products pp " +
            "     JOIN Promotions pr ON pp.promotion_id = pr.promotion_id " +
            "     WHERE pp.product_id = p.product_id " +
            "       AND pr.status = 'Active' " +
            "       AND CURDATE() BETWEEN pr.start_date AND pr.end_date), " +
            "    p.discount_percentage" +
            "  ) AS effective_discount " +
            "FROM products p " +
            "JOIN categories c ON p.category_id = c.category_id " +
            "WHERE c.category_name = ? " +
            "ORDER BY p.product_name ASC";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(mapResultSetToProductForBrowsing(rs));
                }
            }
        }
        return productList;
    }

    // Helper: Map ResultSet to Product Model - used by searchProducts() (ManageProducts).
    // Reads the plain stored discount_percentage.
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setBarcode(rs.getString("barcode"));
        p.setProductName(rs.getString("product_name"));
        p.setBrand(rs.getString("brand"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setTargetGroup(rs.getString("target_group"));
        p.setSize(rs.getString("size"));
        p.setColor(rs.getString("color"));
        p.setBuyingPrice(rs.getBigDecimal("buying_price"));
        p.setSellingPrice(rs.getBigDecimal("selling_price"));
        p.setDiscountPercentage(rs.getBigDecimal("discount_percentage"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setStatus(rs.getString("status"));
        return p;
    }

    // Helper: Map ResultSet to Product Model - used by getProductsByCategory() (SearchProducts).
    // Reads effective_discount (promo-aware). Do NOT reuse this for ManageProducts.
    private Product mapResultSetToProductForBrowsing(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setBarcode(rs.getString("barcode"));
        p.setProductName(rs.getString("product_name"));
        p.setBrand(rs.getString("brand"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setTargetGroup(rs.getString("target_group"));
        p.setSize(rs.getString("size"));
        p.setColor(rs.getString("color"));
        p.setBuyingPrice(rs.getBigDecimal("buying_price"));
        p.setSellingPrice(rs.getBigDecimal("selling_price"));
        p.setDiscountPercentage(rs.getBigDecimal("effective_discount"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setStatus(rs.getString("status"));
        return p;
    }
    
}
