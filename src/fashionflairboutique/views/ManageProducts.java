/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package fashionflairboutique.views;

import fashionflairboutique.data.ProductDAO;
import fashionflairboutique.utils.UIUtils;
import fashionflairboutique.models.Category;
import fashionflairboutique.models.Product;
import fashionflairboutique.models.User;
import java.math.BigDecimal;
import java.util.List;
import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import fashionflairboutique.models.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;


/**
 *
 * @author Chethiya
 */
public class ManageProducts extends javax.swing.JFrame {

    private User currentUser;
    private Timer clockTimer;
    private ProductDAO productDAO = new ProductDAO();
    private int selectedProductId = -1;
    private boolean isEditingSelectedProduct = false;
    /**
     * Creates new form AddProducts
     */
    public ManageProducts() {
        initComponents();
        
    }
    
    public ManageProducts(User loggedInUser) {
        initComponents();
        this.currentUser = loggedInUser;
        
        UIUtils.displayUserDetails(jLblShowUser, currentUser);
        this.clockTimer = UIUtils.startLiveClock(jLblShowDate, jLblShowTime);
        
        initForm();
        attachSearchListeners();

        jTableShowProducts.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTableShowProducts.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });
        
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
    

    // A. Init Data on Window Load
    private void initForm() {
        loadCategoriesToComboBox();
        loadProductsTable(0, "All", "");
        loadStaticComboBoxData();
    }
    
    private void loadCategoriesToComboBox() {
        try {
            ProductDAO dao = new ProductDAO();
            List<Category> categories = dao.getAllCategories();

            jCBCategory.removeAllItems();
            jCBSearchCategory.removeAllItems();

            jCBSearchCategory.addItem(new Category(0, "All Categories", "", "Active"));

            for (Category c : categories) {
                if ("Active".equals(c.getStatus())) {
                    jCBCategory.addItem(c); // Add/Edit only offers assignable (active) categories
                }
                jCBSearchCategory.addItem(c); // search can still find products under an inactive category
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fixed lists from the schema's ENUMs - no DB round-trip needed
    private void loadStaticComboBoxData() {
        jCBSize.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"XS", "S", "M", "L", "XL", "XXL", "Free Size", "Custom"}));
        jCBTargetGroup.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"Men", "Women", "Children", "Unisex"}));
        jCBStatus.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"Active", "Inactive"}));
    }

    private void loadProductsTable(int categoryId, String targetGroup, String keyword) {
        try {
            ProductDAO dao = new ProductDAO();
            List<Product> products = dao.searchProducts(categoryId, targetGroup, keyword);

            DefaultTableModel model = (DefaultTableModel) jTableShowProducts.getModel();
            model.setRowCount(0);

            for (Product p : products) {
                model.addRow(new Object[]{
                    p.getProductId(), p.getBarcode(), p.getProductName(), p.getBrand(),
                    p.getCategoryName(), p.getTargetGroup(), p.getSize(), p.getColor(),
                    p.getBuyingPrice(), p.getSellingPrice(), p.getDiscountPercentage(),
                    p.getStockQuantity(), p.getStatus()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(),
//                "Database Error", JOptionPane.ERROR_MESSAGE);
            UIUtils.showError(jLblMessage, "Error loading products: " + e.getMessage());

        }
    }
    
    private void triggerSearch() {
        Category cat = (Category) jCBSearchCategory.getSelectedItem();
        int catId = (cat != null) ? cat.getCategoryId() : 0;
        String keyword = jTFPName.getText().trim();

        loadProductsTable(catId, "All", keyword);
    }

    private void attachSearchListeners() {
        jCBSearchCategory.addActionListener(evt -> {
            if (!isEditingSelectedProduct) {
                triggerSearch();
            }
        });

        jTFPName.addActionListener(evt -> {
            if (!isEditingSelectedProduct) {
                triggerSearch();
            }
        });
    }
    
    private void populateFormFromSelectedRow() {
        isEditingSelectedProduct = true;

        int row = jTableShowProducts.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) jTableShowProducts.getModel();

        selectedProductId = (int) model.getValueAt(row, 0);
        jTFID.setText(String.valueOf(selectedProductId));
        jTFBarcode.setText(model.getValueAt(row, 1).toString());
        jTFPName.setText(model.getValueAt(row, 2).toString());
        jTFBrand.setText(model.getValueAt(row, 3).toString());

        String categoryName = model.getValueAt(row, 4).toString();
        selectComboBoxItemByCategoryName(jCBCategory, categoryName);

        jCBTargetGroup.setSelectedItem(model.getValueAt(row, 5).toString());
        jCBSize.setSelectedItem(model.getValueAt(row, 6).toString());
        jTFColor.setText(model.getValueAt(row, 7).toString());
        jTFBuyPrice.setText(model.getValueAt(row, 8).toString());
        jTFSellPrice.setText(model.getValueAt(row, 9).toString());
        jTFDiscount.setText(model.getValueAt(row, 10).toString());
        jTFQty.setText(model.getValueAt(row, 11).toString());
        jCBStatus.setSelectedItem(model.getValueAt(row, 12).toString());
    }

    // jCBCategory holds Category objects, not strings - match by name
    private void selectComboBoxItemByCategoryName(javax.swing.JComboBox<Category> cb, String name) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).getCategoryName().equals(name)) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }
    
    private Product buildProductFromForm() {
        Product p = new Product();
        p.setBarcode(jTFBarcode.getText().trim());
        p.setProductName(jTFPName.getText().trim());
        p.setBrand(jTFBrand.getText().trim());

        Category selectedCategory = (Category) jCBCategory.getSelectedItem();
        p.setCategoryId(selectedCategory != null ? selectedCategory.getCategoryId() : 0);

        p.setTargetGroup((String) jCBTargetGroup.getSelectedItem());
        p.setSize((String) jCBSize.getSelectedItem());
        p.setColor(jTFColor.getText().trim());
        p.setStatus((String) jCBStatus.getSelectedItem());

        p.setBuyingPrice(jTFBuyPrice.getText().trim().isEmpty()
            ? BigDecimal.ZERO : new BigDecimal(jTFBuyPrice.getText().trim()));
        p.setSellingPrice(jTFSellPrice.getText().trim().isEmpty()
            ? BigDecimal.ZERO : new BigDecimal(jTFSellPrice.getText().trim()));
//        p.setDiscountPercentage(BigDecimal.ZERO); // set from a discount field if you add one to the form
        p.setDiscountPercentage(jTFDiscount.getText().trim().isEmpty()
            ? BigDecimal.ZERO : new BigDecimal(jTFDiscount.getText().trim()));
        p.setStockQuantity(jTFQty.getText().trim().isEmpty()
            ? 0 : Integer.parseInt(jTFQty.getText().trim()));

        return p;
    }
    
    private void clearForm() {
        selectedProductId = -1;
        isEditingSelectedProduct = false;

        jTFID.setText("");
        jTFBarcode.setText("");
        jTFPName.setText("");
        jTFBrand.setText("");
        if (jCBCategory.getItemCount() > 0) jCBCategory.setSelectedIndex(0);
        if (jCBTargetGroup.getItemCount() > 0) jCBTargetGroup.setSelectedIndex(0);
        if (jCBSize.getItemCount() > 0) jCBSize.setSelectedIndex(0);
        if (jCBStatus.getItemCount() > 0) jCBStatus.setSelectedIndex(0);
        jTFColor.setText("");
        jTFBuyPrice.setText("");
        jTFSellPrice.setText("");
        jTFDiscount.setText("");
        jTFQty.setText("");

        jTableShowProducts.clearSelection();
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
        jLabel2 = new javax.swing.JLabel();
        jTFID = new javax.swing.JTextField();
        jBtnDelete = new javax.swing.JButton();
        jCBSize = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jTFBarcode = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTFPName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTFBrand = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTFColor = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFBuyPrice = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFSellPrice = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFQty = new javax.swing.JTextField();
        jCBCategory = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jCBTargetGroup = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jCBStatus = new javax.swing.JComboBox<>();
        jBtnSave = new javax.swing.JButton();
        jBtnRestock = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableShowProducts = new javax.swing.JTable();
        jLblMessage = new javax.swing.JLabel();
        jBtnResetSearch = new javax.swing.JButton();
        jCBSearchCategory = new javax.swing.JComboBox<>();
        jTFDiscount = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLblShowTime = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLblShowUser = new javax.swing.JLabel();
        jLblShowDate = new javax.swing.JLabel();
        jPanelsidebarPanelContainer = new javax.swing.JPanel();
        jBtnUpdate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1080, 1920));

        jPanel1.setBackground(new java.awt.Color(255, 252, 246));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("ID :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 190, -1, -1));

        jLabel2.setFont(new java.awt.Font("Aarvark Cafe", 1, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(12, 192, 223));
        jLabel2.setText("Manage  Products");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 30, -1, -1));

        jTFID.setEditable(false);
        jTFID.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jTFID, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 220, 190, -1));

        jBtnDelete.setBackground(new java.awt.Color(231, 76, 60));
        jBtnDelete.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnDelete.setText("Delete");
        jBtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 470, -1, -1));

        jCBSize.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jCBSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 310, 190, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Barcode :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 190, -1, -1));

        jTFBarcode.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jTFBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 220, 190, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Product Name :");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 190, -1, -1));

        jTFPName.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jTFPName, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 220, 190, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Brand :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 190, -1, -1));

        jTFBrand.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jTFBrand, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 220, 190, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Category :");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 280, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Size :");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 280, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Color :");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 280, -1, -1));

        jTFColor.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jTFColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 310, 190, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Buying Price :");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 280, -1, -1));

        jTFBuyPrice.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jTFBuyPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 310, 190, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("Selling Price :");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 370, -1, -1));

        jTFSellPrice.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jTFSellPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 400, 190, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Stock Qauntity :");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 370, -1, -1));

        jTFQty.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jTFQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 400, 190, -1));

        jCBCategory.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jCBCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 320, 190, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Select Category to sort :");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 470, -1, -1));

        jCBTargetGroup.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jCBTargetGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 400, 190, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Status");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 370, -1, -1));

        jCBStatus.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jCBStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 400, 190, -1));

        jBtnSave.setBackground(new java.awt.Color(46, 204, 113));
        jBtnSave.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnSave.setText("Save");
        jBtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnSaveActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 470, -1, -1));

        jBtnRestock.setBackground(new java.awt.Color(0, 153, 153));
        jBtnRestock.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnRestock.setText("Restock");
        jBtnRestock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRestockActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnRestock, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 520, -1, -1));

        jTableShowProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Barcode", "Product Name", "Brand", "Category", "Target Group", "Size", "Color", "Buying Price", "Selling Price", "Discount %", "Stock Qty", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableShowProducts);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 570, 1350, 480));

        jLblMessage.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jPanel1.add(jLblMessage, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 130, 540, 30));

        jBtnResetSearch.setBackground(new java.awt.Color(127, 140, 141));
        jBtnResetSearch.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnResetSearch.setText("Reset Search");
        jBtnResetSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnResetSearchActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnResetSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 500, -1, -1));

        jCBSearchCategory.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jCBSearchCategory.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCBSearchCategoryItemStateChanged(evt);
            }
        });
        jPanel1.add(jCBSearchCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 500, 190, -1));

        jTFDiscount.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jTFDiscount, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 490, 190, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setText("Discount Pct :");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 460, -1, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Target Group :");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 370, -1, -1));

        jLblShowTime.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLblShowTime.setText("User :");
        jPanel1.add(jLblShowTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 70, 220, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Date :");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 40, -1, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setText("Time :");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, -1, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setText("User :");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, -1, -1));

        jLblShowUser.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLblShowUser.setText("User :");
        jPanel1.add(jLblShowUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 290, -1));

        jLblShowDate.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLblShowDate.setText("User :");
        jPanel1.add(jLblShowDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 40, 290, -1));

        jPanelsidebarPanelContainer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanelsidebarPanelContainer.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jPanelsidebarPanelContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 1080));

        jBtnUpdate.setBackground(new java.awt.Color(52, 152, 219));
        jBtnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnUpdate.setText("Update");
        jBtnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnUpdateActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 470, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1760, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnSaveActionPerformed
        try {
            Product p = buildProductFromForm();

            if (p.getCategoryId() == 0) {
//                JOptionPane.showMessageDialog(this, "Please select a category.",
//                    "Missing Category", JOptionPane.WARNING_MESSAGE);
                UIUtils.showError(jLblMessage, "Please select a category!!");

                return;
            }

            ProductDAO dao = new ProductDAO();
            if (dao.addProduct(p)) {
//                JOptionPane.showMessageDialog(this, "Product added successfully!");
                UIUtils.showSuccess(jLblMessage, "Product added successfully!");
                clearForm();
                triggerSearch();
            }
        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Enter valid numbers for price/quantity.",
//                "Input Error", JOptionPane.ERROR_MESSAGE);
                UIUtils.showError(jLblMessage, "Enter valid numbers for price/quantity!!");
        } catch (SQLException ex) {
            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
//                "Error", JOptionPane.ERROR_MESSAGE);
                UIUtils.showError(jLblMessage, "Database error: " + ex.getMessage());
        }
    }//GEN-LAST:event_jBtnSaveActionPerformed

    private void jBtnRestockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRestockActionPerformed
        if (selectedProductId == -1) {
            UIUtils.showError(jLblMessage, "Please select a product to restock.");
            return;
        }

        String quantityStr = jTFQty.getText().trim();
        if (quantityStr.isEmpty() || !quantityStr.matches("\\d+")) {
            UIUtils.showError(jLblMessage, "Please enter a valid positive quantity.");
            return;
        }

        int qtyToAdd = Integer.parseInt(quantityStr);
        if (qtyToAdd <= 0) {
            UIUtils.showError(jLblMessage, "Quantity must be greater than zero.");
            return;
        }

        try {
            int userId = (currentUser != null) ? currentUser.getUserId() : 1;

            boolean success = productDAO.restockProduct(selectedProductId, qtyToAdd, userId);

            if (success) {
                UIUtils.showSuccess(jLblMessage, "Stock restocked and inventory log created!");
                clearForm();
                triggerSearch();
            } else {
                UIUtils.showError(jLblMessage, "Failed to restock product.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            UIUtils.showError(jLblMessage, "Database error: " + ex.getMessage());
        }
    }//GEN-LAST:event_jBtnRestockActionPerformed

    private void jBtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnDeleteActionPerformed
       if (selectedProductId == -1) {
//            JOptionPane.showMessageDialog(this, "Select a product from the table before deleting.",
//                "No Product Selected", JOptionPane.WARNING_MESSAGE);
                UIUtils.showError(jLblMessage, "Select a product from the table before deleting");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this product?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            ProductDAO dao = new ProductDAO();
            if (dao.deleteProduct(selectedProductId)) {
//                JOptionPane.showMessageDialog(this, "Product deleted.");
                UIUtils.showSuccess(jLblMessage, "Product deleted!!");
                clearForm();
                triggerSearch();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
//                "Error", JOptionPane.ERROR_MESSAGE);
                UIUtils.showError(jLblMessage, "Database error: " + ex.getMessage());
        }
    }//GEN-LAST:event_jBtnDeleteActionPerformed

    private void jBtnResetSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnResetSearchActionPerformed
        jCBSearchCategory.setSelectedIndex(0); // "All Categories"
        jTFPName.setText("");
        isEditingSelectedProduct = false;
        selectedProductId = -1;
        triggerSearch();
    }//GEN-LAST:event_jBtnResetSearchActionPerformed

    private void jCBSearchCategoryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCBSearchCategoryItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED && !isEditingSelectedProduct) {
            triggerSearch();
        }
    }//GEN-LAST:event_jCBSearchCategoryItemStateChanged

    private void jBtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnUpdateActionPerformed
        if (selectedProductId == -1) {
            UIUtils.showError(jLblMessage, "Please select a product from the table to update!");
            return;
        }

        try {
            // 1. Build updated product from form fields
            Product p = buildProductFromForm();
            p.setProductId(selectedProductId); // Set the active ID for SQL WHERE clause

            if (p.getCategoryId() == 0) {
                UIUtils.showError(jLblMessage, "Please select a valid category!");
                return;
            }

            // 2. Perform DB update
            boolean success = productDAO.updateProduct(p);

            if (success) {
                UIUtils.showSuccess(jLblMessage, "Product updated successfully!");
                clearForm();
                triggerSearch(); // Reload table data from DB
            } else {
                UIUtils.showError(jLblMessage, "Failed to update product details.");
            }
        } catch (NumberFormatException ex) {
            UIUtils.showError(jLblMessage, "Enter valid numbers for prices and quantity!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            UIUtils.showError(jLblMessage, "Database error: " + ex.getMessage());
        }
    }//GEN-LAST:event_jBtnUpdateActionPerformed

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
            java.util.logging.Logger.getLogger(ManageProducts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageProducts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageProducts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageProducts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageProducts().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnDelete;
    private javax.swing.JButton jBtnResetSearch;
    private javax.swing.JButton jBtnRestock;
    private javax.swing.JButton jBtnSave;
    private javax.swing.JButton jBtnUpdate;
    private javax.swing.JComboBox<Category> jCBCategory;
    private javax.swing.JComboBox<Category> jCBSearchCategory;
    private javax.swing.JComboBox<String> jCBSize;
    private javax.swing.JComboBox<String> jCBStatus;
    private javax.swing.JComboBox<String> jCBTargetGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLblMessage;
    private javax.swing.JLabel jLblShowDate;
    private javax.swing.JLabel jLblShowTime;
    private javax.swing.JLabel jLblShowUser;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelsidebarPanelContainer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFBarcode;
    private javax.swing.JTextField jTFBrand;
    private javax.swing.JTextField jTFBuyPrice;
    private javax.swing.JTextField jTFColor;
    private javax.swing.JTextField jTFDiscount;
    private javax.swing.JTextField jTFID;
    private javax.swing.JTextField jTFPName;
    private javax.swing.JTextField jTFQty;
    private javax.swing.JTextField jTFSellPrice;
    private javax.swing.JTable jTableShowProducts;
    // End of variables declaration//GEN-END:variables
}
