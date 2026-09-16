/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Chethiya
 */
public class POSDAO {
    public boolean processSale(int userId, double totalAmount, String paymentType, DefaultTableModel cartModel) {
        String insertSaleSQL = "INSERT INTO sales (user_id, total_amount, payment_type, created_at) VALUES (?, ?, ?, NOW())";
        String insertItemSQL = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        String updateStockSQL = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnector.getInstance().getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert Sales Header
            int saleId = -1;
            try (PreparedStatement psSale = conn.prepareStatement(insertSaleSQL, Statement.RETURN_GENERATED_KEYS)) {
                psSale.setInt(1, userId);
                psSale.setDouble(2, totalAmount);
                psSale.setString(3, paymentType);
                psSale.executeUpdate();

                try (ResultSet rs = psSale.getGeneratedKeys()) {
                    if (rs.next()) {
                        saleId = rs.getInt(1);
                    }
                }
            }

            if (saleId == -1) {
                conn.rollback();
                return false;
            }

            // 2. Insert Sale Items & Deduct Inventory Stock
            try (PreparedStatement psItem = conn.prepareStatement(insertItemSQL);
                 PreparedStatement psStock = conn.prepareStatement(updateStockSQL)) {

                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    int productId = Integer.parseInt(cartModel.getValueAt(i, 0).toString());
                    int qty = Integer.parseInt(cartModel.getValueAt(i, 3).toString());
                    double unitPrice = Double.parseDouble(cartModel.getValueAt(i, 4).toString());
                    double subtotal = Double.parseDouble(cartModel.getValueAt(i, 5).toString());

                    // Add to sale_items
                    psItem.setInt(1, saleId);
                    psItem.setInt(2, productId);
                    psItem.setInt(3, qty);
                    psItem.setDouble(4, unitPrice);
                    psItem.setDouble(5, subtotal);
                    psItem.addBatch();

                    // Update stock in products table
                    psStock.setInt(1, qty);
                    psStock.setInt(2, productId);
                    psStock.addBatch();
                }

                psItem.executeBatch();
                psStock.executeBatch();
            }

            conn.commit(); // Save transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Undo all changes if an error occurs
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
