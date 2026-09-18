/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.data;

import fashionflairboutique.models.Category;
import fashionflairboutique.data.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Chethiya
 */
public class CategoryDAO {
    public List<Category> getAllCategories() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY category_id DESC";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryId(rs.getInt("category_id"));
                cat.setCategoryName(rs.getString("category_name"));
                cat.setDescription(rs.getString("description"));
                cat.setStatus(rs.getString("status"));
                list.add(cat);
            }
        }
        return list;
    }

    public boolean addCategory(String name, String description, String status) throws SQLException {
        String sql = "INSERT INTO categories (category_name, description, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, description.trim());
            ps.setString(3, status);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateCategory(int id, String name, String description, String status) throws SQLException {
        String sql = "UPDATE categories SET category_name = ?, description = ?, status = ? WHERE category_id = ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, description.trim());
            ps.setString(3, status);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean categoryNameExists(String name, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categories WHERE LOWER(category_name) = LOWER(?) AND category_id != ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
