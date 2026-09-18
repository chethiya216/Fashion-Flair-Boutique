/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package fashionflairboutique.views;

import fashionflairboutique.models.User;
import fashionflairboutique.data.DiscountDAO;
import fashionflairboutique.utils.UIUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Chethiya
 */
public class DiscountsFrame extends javax.swing.JFrame {

    private User currentUser;
    private Timer clockTimer;
    private DiscountDAO discountsDAO = new DiscountDAO();
    private int selectedPromotionId = -1;
    /**
     * Creates new form Discounts
     */
    public DiscountsFrame() {
        initComponents();
    }
    
    public DiscountsFrame(User loggedInUser){
        initComponents();
        this.currentUser = loggedInUser;
        
        UIUtils.displayUserDetails(jLblShowUser, currentUser);
        this.clockTimer = UIUtils.startLiveClock(jLblShowDate, jLblShowTime);
        
        loadActiveCampaignsTable();
        loadDropdownData();
        jTableActiveCamps.getSelectionModel().addListSelectionListener(evt -> {
        if (!evt.getValueIsAdjusting() && jTableActiveCamps.getSelectedRow() != -1) {
            populateFormFromSelectedPromotion();
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
    
    private void loadActiveCampaignsTable(){
        DefaultTableModel model = (DefaultTableModel) jTableActiveCamps.getModel();
        model.setRowCount(0); // Clear existing rows

        List<Object[]> campaigns = discountsDAO.getAllPromotions();
        for (Object[] row : campaigns) {
            model.addRow(row);
        }
    }
    
    private void loadCategoryLinksForPromotion(int promotionId) {
        DefaultTableModel model = (DefaultTableModel) jTableCategoryPromo.getModel();
        model.setRowCount(0);
        List<Object[]> links = discountsDAO.getCategoryLinksForPromotion(promotionId);
        for (Object[] row : links) {
            model.addRow(row);
        }
    }
    
    private void loadDropdownData() {
        // 1. Populate Status Combo Box manually or from DB
        jCBStatus.removeAllItems();
        jCBStatus.addItem("Active");
        jCBStatus.addItem("Inactive");
        jCBStatus.addItem("Scheduled");

        // 2. Populate Categories Combo Box from DAO
        jCBCategories.removeAllItems();
        jCBCategories.addItem("Select Category...");
        List<String> categories = discountsDAO.getAllCategories(); // Ensure you have this DAO method
        for (String cat : categories) {
            jCBCategories.addItem(cat);
        }

        // 3. Populate Target Group Combo Box from DAO
        jCBTargetGroup.removeAllItems();
        jCBTargetGroup.addItem("Select Target...");
        List<String> targetGroups = discountsDAO.getAllTargetGroups(); // Ensure you have this DAO method
        for (String group : targetGroups) {
            jCBTargetGroup.addItem(group);
        }
    }
    
    private void populateFormFromSelectedPromotion() {
        int row = jTableActiveCamps.getSelectedRow();
        selectedPromotionId = (int) jTableActiveCamps.getValueAt(row, 0);

        jTFPromoName.setText(jTableActiveCamps.getValueAt(row, 1).toString());

        Object descVal = jTableActiveCamps.getValueAt(row, 2);
        jTADescription.setText(descVal != null ? descVal.toString() : "");

        jTFDiscountPct.setText(jTableActiveCamps.getValueAt(row, 3).toString()); // was 2

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            Object startVal = jTableActiveCamps.getValueAt(row, 4); // was 3
            Object endVal = jTableActiveCamps.getValueAt(row, 5);   // was 4
            jDateChooserStart.setDate(startVal != null ? sdf.parse(startVal.toString()) : null);
            jDateChooserEnd.setDate(endVal != null ? sdf.parse(endVal.toString()) : null);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        jCBStatus.setSelectedItem(jTableActiveCamps.getValueAt(row, 6).toString()); // was 5

        loadCategoryLinksForPromotion(selectedPromotionId);
    }
    
    private void clearForm() {
        selectedPromotionId = -1; 
        jTFPromoName.setText("");
        jTFDiscountPct.setText("");
        jDateChooserStart.setDate(null);
        jDateChooserEnd.setDate(null);
        jCBStatus.setSelectedIndex(0);
        jTADescription.setText("");
        jCBCategories.setSelectedIndex(0);
        jCBTargetGroup.setSelectedIndex(0);
        jTableActiveCamps.clearSelection();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableActiveCamps = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jBtnDeactivate = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jBtnCancel = new javax.swing.JButton();
        jTFPromoName = new javax.swing.JTextField();
        jDateChooserEnd = new com.toedter.calendar.JDateChooser();
        jDateChooserStart = new com.toedter.calendar.JDateChooser();
        jCBStatus = new javax.swing.JComboBox<>();
        jTFDiscountPct = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTADescription = new javax.swing.JTextArea();
        jBtnSave = new javax.swing.JButton();
        jCBTargetGroup = new javax.swing.JComboBox<>();
        jCBCategories = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCategoryPromo = new javax.swing.JTable();
        jLblShowUser = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLblShowDate = new javax.swing.JLabel();
        jLblShowTime = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jBtnRemove = new javax.swing.JButton();
        jBtnAddCategoryPromo = new javax.swing.JButton();
        jPanelsidebarPanelContainer = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 252, 246));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Aarvark Cafe", 0, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(12, 192, 223));
        jLabel2.setText("Manage Discounts and Promotions");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 40, -1, 80));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Description :");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 670, -1, -1));

        jTableActiveCamps.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jTableActiveCamps.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Promo ID", "Name", "Description", "Discount %", "Start Date", "End Date", "Status", "Created By"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableActiveCamps);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 190, 1250, 140));

        jLabel13.setFont(new java.awt.Font("Aarvark Cafe", 0, 24)); // NOI18N
        jLabel13.setText("Active Campaigns");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 150, -1, -1));

        jLabel14.setFont(new java.awt.Font("Aarvark Cafe", 0, 24)); // NOI18N
        jLabel14.setText("Campaign Details");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 370, -1, -1));

        jLabel15.setFont(new java.awt.Font("Aarvark Cafe", 0, 24)); // NOI18N
        jLabel15.setText("Select Catagory  for the Promo");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 380, -1, -1));

        jBtnDeactivate.setBackground(new java.awt.Color(127, 140, 141));
        jBtnDeactivate.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnDeactivate.setText("Deactivate Selected Promo");
        jBtnDeactivate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnDeactivateActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnDeactivate, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 800, -1, 40));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Target Group :");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 430, -1, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Promotion Name :");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 430, -1, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setText("Discount Pct :");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 470, -1, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setText("Start Date :");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 520, -1, -1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setText("End Date :");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 570, -1, -1));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel21.setText("Status :");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 620, -1, -1));

        jBtnCancel.setBackground(new java.awt.Color(231, 76, 60));
        jBtnCancel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnCancel.setText("Cancel");
        jBtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 800, -1, 40));

        jTFPromoName.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jPanel1.add(jTFPromoName, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 420, 200, -1));

        jDateChooserEnd.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jPanel1.add(jDateChooserEnd, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 570, 200, 30));

        jDateChooserStart.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jPanel1.add(jDateChooserStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 520, 200, 30));

        jCBStatus.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jPanel1.add(jCBStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 620, 200, -1));

        jTFDiscountPct.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jPanel1.add(jTFDiscountPct, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 470, 200, -1));

        jTADescription.setColumns(20);
        jTADescription.setRows(5);
        jScrollPane3.setViewportView(jTADescription);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 670, -1, -1));

        jBtnSave.setBackground(new java.awt.Color(46, 204, 113));
        jBtnSave.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnSave.setText("Save Campaign");
        jBtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnSaveActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 800, -1, 40));

        jCBTargetGroup.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jPanel1.add(jCBTargetGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 460, 200, -1));

        jCBCategories.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jPanel1.add(jCBCategories, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 460, 200, -1));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setText("Categories");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 430, -1, -1));

        jTableCategoryPromo.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jTableCategoryPromo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Category", "Target Group", "Promotion"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableCategoryPromo);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 520, 600, 230));

        jLblShowUser.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLblShowUser.setText("User :");
        jPanel1.add(jLblShowUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, 290, -1));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel23.setText("User :");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, -1, -1));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setText("Date :");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, -1, -1));

        jLblShowDate.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLblShowDate.setText("User :");
        jPanel1.add(jLblShowDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, 290, -1));

        jLblShowTime.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLblShowTime.setText("User :");
        jPanel1.add(jLblShowTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 90, 220, -1));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setText("Time :");
        jPanel1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 90, -1, -1));

        jBtnRemove.setBackground(new java.awt.Color(231, 76, 60));
        jBtnRemove.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnRemove.setText("Remove ");
        jBtnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRemoveActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 780, -1, 40));

        jBtnAddCategoryPromo.setBackground(new java.awt.Color(46, 204, 113));
        jBtnAddCategoryPromo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jBtnAddCategoryPromo.setText("Add Category to Promo");
        jBtnAddCategoryPromo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAddCategoryPromoActionPerformed(evt);
            }
        });
        jPanel1.add(jBtnAddCategoryPromo, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 780, -1, 40));

        jPanelsidebarPanelContainer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanelsidebarPanelContainer.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jPanelsidebarPanelContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 890));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1725, Short.MAX_VALUE)
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
            String name = jTFPromoName.getText().trim();
            String discountText = jTFDiscountPct.getText().trim();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDateObj = jDateChooserStart.getDate();
            String startDate = (startDateObj != null) ? sdf.format(startDateObj) : "";

            Date endDateObj = jDateChooserEnd.getDate();
            String endDate = (endDateObj != null) ? sdf.format(endDateObj) : "";

            String status = jCBStatus.getSelectedItem().toString();
            String description = jTADescription.getText().trim();

            if (name.isEmpty() || discountText.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required campaign fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double discount = Double.parseDouble(discountText);

            // Validate discount percentage range (0 to 100)
            if (discount < 0 || discount > 100) {
                JOptionPane.showMessageDialog(this, "Discount percentage must be between 0 and 100.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate that end date is after start date
            if (endDateObj.before(startDateObj) || endDateObj.equals(startDateObj)) {
                JOptionPane.showMessageDialog(this, "End date must be after the start date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int currentAdminId = 1; // Replace with your active session user ID session logic

            boolean success;
            String successMessage;

            if (selectedPromotionId == -1) {
                // No row selected - creating a brand new promotion
                success = discountsDAO.createPromotion(name, description, discount, startDate, endDate, status, currentAdminId);
                successMessage = "General promotion saved successfully!";
            } else {
                // A row is selected - update that existing promotion instead of inserting a new one
                success = discountsDAO.updatePromotion(selectedPromotionId, name, description, discount, startDate, endDate, status);
                successMessage = "Promotion updated successfully!";
            }

            if (success) {
                JOptionPane.showMessageDialog(this, successMessage);
                loadActiveCampaignsTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save promotion.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Discount must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jBtnSaveActionPerformed

    private void jBtnDeactivateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnDeactivateActionPerformed
        int selectedRow = jTableActiveCamps.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a campaign from the Active Campaigns table to deactivate.", "Selection Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int promoId = (int) jTableActiveCamps.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to deactivate promotion ID: " + promoId + "?", "Confirm Deactivation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = discountsDAO.updatePromotionStatus(promoId, "Inactive");
            if (success) {
                JOptionPane.showMessageDialog(this, "Promotion deactivated successfully.");
                loadActiveCampaignsTable(); // Refresh table view
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update promotion status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jBtnDeactivateActionPerformed

    private void jBtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCancelActionPerformed
        clearForm();
    }//GEN-LAST:event_jBtnCancelActionPerformed

    private void jBtnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRemoveActionPerformed
        int row = jTableCategoryPromo.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a category link from the table first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedPromotionId == -1) {
            JOptionPane.showMessageDialog(this, "Select a promotion first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String categoryName = jTableCategoryPromo.getValueAt(row, 0).toString();
        String targetGroup = jTableCategoryPromo.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove " + categoryName + " (" + targetGroup + ") from this promotion?",
            "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int removed = discountsDAO.unlinkCategoryFromPromotion(selectedPromotionId, categoryName, targetGroup);
            if (removed > 0) {
                JOptionPane.showMessageDialog(this, "Removed " + removed + " product link(s).");
                loadCategoryLinksForPromotion(selectedPromotionId);
            } else {
                JOptionPane.showMessageDialog(this, "Nothing removed.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jBtnRemoveActionPerformed

    private void jBtnAddCategoryPromoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAddCategoryPromoActionPerformed
        if (selectedPromotionId == -1) {
            JOptionPane.showMessageDialog(this, "Select a promotion from the Active Campaigns table first.",
                "No Promotion Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedCategory = (String) jCBCategories.getSelectedItem();
        if (selectedCategory == null || selectedCategory.equals("Select Category...")) {
            JOptionPane.showMessageDialog(this, "Please select a category.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedTargetGroup = (String) jCBTargetGroup.getSelectedItem();
        if (selectedTargetGroup == null || selectedTargetGroup.equals("Select Target...")) {
            JOptionPane.showMessageDialog(this, "Please select a target group.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int linkedCount = discountsDAO.linkCategoryToPromotion(selectedPromotionId, selectedCategory, selectedTargetGroup);

            if (linkedCount > 0) {
                JOptionPane.showMessageDialog(this, linkedCount + " product(s) linked to this promotion.");
                loadCategoryLinksForPromotion(selectedPromotionId);
            } else {
                JOptionPane.showMessageDialog(this, "No matching products found, or they're already linked.",
                    "Nothing to Link", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Cannot Link Promotion", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jBtnAddCategoryPromoActionPerformed

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
            java.util.logging.Logger.getLogger(DiscountsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DiscountsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DiscountsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DiscountsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DiscountsFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnAddCategoryPromo;
    private javax.swing.JButton jBtnCancel;
    private javax.swing.JButton jBtnDeactivate;
    private javax.swing.JButton jBtnRemove;
    private javax.swing.JButton jBtnSave;
    private javax.swing.JComboBox<String> jCBCategories;
    private javax.swing.JComboBox<String> jCBStatus;
    private javax.swing.JComboBox<String> jCBTargetGroup;
    private com.toedter.calendar.JDateChooser jDateChooserEnd;
    private com.toedter.calendar.JDateChooser jDateChooserStart;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLblShowDate;
    private javax.swing.JLabel jLblShowTime;
    private javax.swing.JLabel jLblShowUser;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelsidebarPanelContainer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTADescription;
    private javax.swing.JTextField jTFDiscountPct;
    private javax.swing.JTextField jTFPromoName;
    private javax.swing.JTable jTableActiveCamps;
    private javax.swing.JTable jTableCategoryPromo;
    // End of variables declaration//GEN-END:variables
}
