/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.data;

import fashionflairboutique.models.SalesReport;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Chethiya
 */
public class SalesReportDAO {
    // Filter by Date and Reference No
    public List<SalesReport> searchSales(String invoiceNo, String searchDate) {
        List<SalesReport> salesList = new ArrayList<>();

        // Base query with 'WHERE 1=1' to easily append conditions dynamically
        StringBuilder sql = new StringBuilder(
            "SELECT s.sale_id, s.invoice_number, u.username AS cashier, s.subtotal, s.total_discount, s.net_total, s.sale_date " +
            "FROM sales s JOIN users u ON s.user_id = u.user_id WHERE 1=1"
        );

        boolean hasInvoice = invoiceNo != null && !invoiceNo.trim().isEmpty();
        boolean hasDate = searchDate != null && !searchDate.trim().isEmpty();

        // Dynamically append filters only if the user provided them
        if (hasInvoice) {
            sql.append(" AND s.invoice_number LIKE ?");
        }
        if (hasDate) {
            sql.append(" AND DATE(s.sale_date) = ?");
        }

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasInvoice) {
                stmt.setString(paramIndex++, "%" + invoiceNo.trim() + "%");
            }
            if (hasDate) {
                stmt.setString(paramIndex++, searchDate);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    salesList.add(new SalesReport(
                        rs.getInt("sale_id"),
                        rs.getString("invoice_number"),
                        rs.getString("cashier"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("total_discount"),
                        rs.getDouble("net_total"),
                        rs.getString("sale_date")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salesList;
    }
}


