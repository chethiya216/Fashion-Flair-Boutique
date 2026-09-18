/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package fashionflairboutique.views;

import fashionflairboutique.models.User;
import fashionflairboutique.utils.UIUtils;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import fashionflairboutique.data.ProductCatalogDAO;
import fashionflairboutique.data.ProductDAO;
import fashionflairboutique.models.Category;
import fashionflairboutique.models.PCatalog;
import fashionflairboutique.models.Product;
import java.util.List;
import java.sql.*;

/**
 *
 * @author Chethiya
 */
public class ProductCatalog extends javax.swing.JFrame {

    private User currentUser;
    private Timer clockTimer;
    private ProductCatalogDAO catalogDAO;
    private int selectedProductId = -1;
    /**
     * Creates new form ProductCatalog
     */
    public ProductCatalog() {
        initComponents();
        this.catalogDAO = new ProductCatalogDAO();
    }
    
    
    public ProductCatalog(User loggedInUser){
        initComponents();
        this.currentUser = loggedInUser;
        this.catalogDAO = new ProductCatalogDAO();
        
        UIUtils.displayUserDetails(jLblShowUser, currentUser);
        this.clockTimer = UIUtils.startLiveClock(jLblShowDate, jLblShowTime);
        attachSearchListeners();
        filterCatalog(); // Load all products initially
        loadCategoriesToComboBox(); // Loads categories AND calls filterCatalog()

        
        if (this.currentUser != null) {
            // 1. Set BorderLayout on your newly created side panel
            jPanelsidebarPanelContainer.setLayout(new java.awt.BorderLayout());

            // 2. Instantiate your custom SidebarPanel
            SidebarPanel sidebar = new SidebarPanel(this, currentUser);

            // 3. Add sidebar to the side panel container
            jPanelsidebarPanelContainer.add(sidebar, java.awt.BorderLayout.CENTER);

            // 4. Refresh both the container and the frame
            jPanelsidebarPanelContainer.revalidate();
            jPanelsidebarPanelContainer.repaint();
        }
    }
    
    
    private void loadCategoriesToComboBox() {
        try {
            ProductDAO dao = new ProductDAO();
            List<Category> categories = dao.getAllCategories();

            jCBCategory.removeAllItems();
            jCBCategory.addItem("All Categories");

            for (Category c : categories) {
                if ("Active".equalsIgnoreCase(c.getStatus())) {
                    jCBCategory.addItem(c.getCategoryName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (jLblMessage != null) {
                UIUtils.showError(jLblMessage, "Error loading categories: " + e.getMessage());
            }
        }
    }
    
    private void attachSearchListeners() {
        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterCatalog(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterCatalog(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterCatalog(); }
        };

        // Attach to barcode textfield
        jTFBarcode.getDocument().addDocumentListener(dl);

        // Attach to product name textfield
        jTFPName.getDocument().addDocumentListener(dl);

        // Attach to category combobox
        jCBCategory.addActionListener(e -> filterCatalog());
    }

    private void filterCatalog() {
        String barcode = jTFBarcode.getText().trim();
        String productName = jTFPName.getText().trim();
        String selectedCategory = (jCBCategory.getSelectedItem() != null) 
                ? jCBCategory.getSelectedItem().toString() 
                : "All Categories";

        // Changed List<Product> to List<PCatalog>
        List<PCatalog> list = catalogDAO.searchCatalog(barcode, productName, selectedCategory);
        populateCatalogTable(list);
    }

    private void populateCatalogTable(List<PCatalog> list) {
        DefaultTableModel model = (DefaultTableModel) jTableCatalog.getModel();
        model.setRowCount(0); // Clear existing rows

        for (PCatalog pc : list) {
            model.addRow(new Object[]{
                pc.getProductId(),            // Col 0: Integer (ProductID)
                pc.getBarcode(),              // Col 1: String  (Barcode)
                pc.getProductName(),          // Col 2: String  (Product Name)
                pc.getBrand(),                // Col 3: String  (Brand)
                pc.getCategoryName(),         // Col 4: String  (Category)
                pc.getTargetGroup(),          // Col 5: String  (Target Group) - or "" if null
                pc.getSize(),                 // Col 6: String  (Size) - or "" if null
                pc.getColor(),                // Col 7: String  (Color) - or "" if null
                pc.getSellingPrice(),         // Col 8: Double  (Price)
                pc.getDiscountedPrice(),      // Col 9: Double  (Discounted Price)
                pc.getStockQuantity(),        // Col 10: Integer (Qty)
                pc.getStatus()                // Col 11: String  (Status)
            });
        }
    }
    

    @Override
    public void dispose() {
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop(); // Stop clock timer when frame closes
        }
        super.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLblShowTime = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLblShowUser = new javax.swing.JLabel();
        jLblShowDate = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTFBarcode = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFPName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCatalog = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jCBCategory = new javax.swing.JComboBox<>();
        jLblMessage = new javax.swing.JLabel();
        jPanelsidebarPanelContainer = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 252, 246));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Aarvark Cafe", 0, 24)); // NOI18N
        jLabel1.setText("Search Product ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 190, 150, -1));

        jLblShowTime.setFont(new java.awt.Font("Aarvark Cafe", 0, 18)); // NOI18N
        jLblShowTime.setText("User :");
        jPanel1.add(jLblShowTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 80, 220, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Date :");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 50, -1, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setText("Time :");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 80, -1, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setText("User :");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 20, -1, -1));

        jLblShowUser.setFont(new java.awt.Font("Aarvark Cafe", 0, 18)); // NOI18N
        jLblShowUser.setText("User :");
        jPanel1.add(jLblShowUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 290, -1));

        jLblShowDate.setFont(new java.awt.Font("Aarvark Cafe", 0, 18)); // NOI18N
        jLblShowDate.setText("User :");
        jPanel1.add(jLblShowDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 50, 290, -1));

        jLabel2.setFont(new java.awt.Font("Aarvark Cafe", 1, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(12, 192, 223));
        jLabel2.setText("Product Catalog");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 40, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Barcode :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 240, -1, -1));

        jTFBarcode.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jPanel1.add(jTFBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 270, 220, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Category :");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 400, -1, -1));

        jTFPName.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jPanel1.add(jTFPName, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 350, 220, -1));

        jTableCatalog.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jTableCatalog.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ProductID", "Barcode", "Product Name", "Brand", "Category", "Target Group", "Size", "Color", "Price", "Discounted Price", "Qty", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableCatalog);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 190, 1150, 610));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Product Name :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 320, -1, -1));

        jCBCategory.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCBCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Category" }));
        jPanel1.add(jCBCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 430, 220, 40));

        jLblMessage.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jPanel1.add(jLblMessage, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 150, 540, 30));

        jPanelsidebarPanelContainer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanelsidebarPanelContainer.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jPanelsidebarPanelContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 830));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1726, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProductCatalog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProductCatalog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProductCatalog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProductCatalog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProductCatalog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jCBCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLblMessage;
    private javax.swing.JLabel jLblShowDate;
    private javax.swing.JLabel jLblShowTime;
    private javax.swing.JLabel jLblShowUser;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelsidebarPanelContainer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFBarcode;
    private javax.swing.JTextField jTFPName;
    private javax.swing.JTable jTableCatalog;
    // End of variables declaration//GEN-END:variables
}
