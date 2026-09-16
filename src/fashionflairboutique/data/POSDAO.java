/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.data;

import fashionflairboutique.models.POS;
import fashionflairboutique.models.Product;
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
    
    
    public boolean processSale(int userId, String paymentMethod, DefaultTableModel cartModel) {
        String insertSaleSQL = "INSERT INTO Sales (invoice_number, user_id, subtotal, total_discount, net_total) " +
                                "VALUES (?, ?, ?, ?, ?)";
        String insertItemSQL = "INSERT INTO Sale_Items (sale_id, product_id, quantity, unit_price, discount_applied, line_total) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";
        String insertPaymentSQL = "INSERT INTO Payments (sale_id, payment_method, amount_paid) VALUES (?, ?, ?)";
        String updateStockSQL = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
        String logInventorySQL = "INSERT INTO Inventory_Logs (product_id, change_type, quantity_changed, previous_stock, new_stock, logged_by) " +
                                  "SELECT ?, 'SALE', ?, stock_quantity + ?, stock_quantity, ? FROM products WHERE product_id = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnector.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Compute totals from the cart itself (never trust a client-supplied total)
            double subtotal = 0.0;
            double totalDiscount = 0.0;

            int rowCount = cartModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                int qty = Integer.parseInt(cartModel.getValueAt(i, 3).toString());
                double unitPrice = Double.parseDouble(cartModel.getValueAt(i, 4).toString());
                double lineTotal = Double.parseDouble(cartModel.getValueAt(i, 5).toString());

                double lineSubtotal = unitPrice * qty;
                double lineDiscount = lineSubtotal - lineTotal; // total discount for this line

                subtotal += lineSubtotal;
                totalDiscount += lineDiscount;
            }
            double netTotal = subtotal - totalDiscount;

            // 2. Insert Sales header
            String invoiceNumber = "INV-" + System.currentTimeMillis();
            int saleId = -1;

            try (PreparedStatement psSale = conn.prepareStatement(insertSaleSQL, Statement.RETURN_GENERATED_KEYS)) {
                psSale.setString(1, invoiceNumber);
                psSale.setInt(2, userId);
                psSale.setDouble(3, subtotal);
                psSale.setDouble(4, totalDiscount);
                psSale.setDouble(5, netTotal);
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

            // 3. Insert Sale_Items, deduct stock, log inventory change - one row at a time
            //    (kept as individual statements rather than batched, so each line's
            //    discount/quantity can be computed independently)
            try (PreparedStatement psItem = conn.prepareStatement(insertItemSQL);
                 PreparedStatement psStock = conn.prepareStatement(updateStockSQL);
                 PreparedStatement psLog = conn.prepareStatement(logInventorySQL)) {

                for (int i = 0; i < rowCount; i++) {
                    int productId = Integer.parseInt(cartModel.getValueAt(i, 0).toString());
                    int qty = Integer.parseInt(cartModel.getValueAt(i, 3).toString());
                    double unitPrice = Double.parseDouble(cartModel.getValueAt(i, 4).toString());
                    double lineTotal = Double.parseDouble(cartModel.getValueAt(i, 5).toString());

                    double lineSubtotal = unitPrice * qty;
                    double lineDiscountTotal = lineSubtotal - lineTotal;
                    double discountAppliedPerUnit = qty > 0 ? (lineDiscountTotal / qty) : 0.0;

                    psItem.setInt(1, saleId);
                    psItem.setInt(2, productId);
                    psItem.setInt(3, qty);
                    psItem.setDouble(4, unitPrice);
                    psItem.setDouble(5, discountAppliedPerUnit);
                    psItem.setDouble(6, lineTotal);
                    psItem.executeUpdate();

                    psStock.setInt(1, qty);
                    psStock.setInt(2, productId);
                    psStock.executeUpdate();

                    psLog.setInt(1, productId);
                    psLog.setInt(2, -qty);
                    psLog.setInt(3, qty); // previous_stock = current (already decremented) + qty
                    psLog.setInt(4, userId);
                    psLog.setInt(5, productId);
                    psLog.executeUpdate();
                }
            }

            // 4. Insert Payment record
            try (PreparedStatement psPayment = conn.prepareStatement(insertPaymentSQL)) {
                psPayment.setInt(1, saleId);
                psPayment.setString(2, paymentMethod);
                psPayment.setDouble(3, netTotal);
                psPayment.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
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
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                // NOTE: not calling conn.close() here - DatabaseConnector is a
                // singleton that reuses one shared Connection app-wide (see
                // DatabaseConnector.getInstance()). Closing it here would break
                // every other DAO call made after this one in the same session.
            }
        }
    }

    /**
     * FIX: now actually builds and returns a POS object (the original
     * built a Product and tried to return it from a method declared to
     * return POS - a type mismatch that would not compile).
     * FIX: status comparison now matches the schema's actual casing
     * ('Active', not 'ACTIVE').
     */
    public POS getProductByBarcode(String barcodeText) {
        String sql = "SELECT * FROM products WHERE barcode = ? AND status = 'Active'";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, barcodeText.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    POS pos = new POS();
                    pos.setProductId(rs.getInt("product_id"));
                    pos.setBarcode(rs.getString("barcode"));
                    pos.setProductName(rs.getString("product_name"));
                    pos.setPrice(rs.getDouble("selling_price"));
                    pos.setAvailableQty(rs.getInt("stock_quantity"));
                    pos.setDiscount(rs.getDouble("discount_percentage")); // product's standing discount - prefilled default
                    return pos;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
