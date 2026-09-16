/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.data;


import fashionflairboutique.models.Product;

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

    // 5. Combined Search (By Category, Target Group, or Keyword)
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

    // Helper: Map ResultSet to Product Model
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
    
}
