/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.models;

/**
 *
 * @author Chethiya
 */
public class PCatalog {
    private int productId;
    private String barcode;
    private String productName;
    private int categoryId;
    private String categoryName;
    private String brand;
    private String targetGroup;
    private String size;
    private String color;
    private double sellingPrice;
    private double discountPercentage;
    private int stockQuantity;
    private String status;

    // Default Constructor (Required for dynamic initialization)
    public PCatalog() {
        
    }

    // Parameterized Constructor
    public PCatalog(int productId, String barcode, String productName, int categoryId, 
                    String categoryName, String brand, String targetGroup, String size, 
                    String color, double sellingPrice, double discountPercentage, 
                    int stockQuantity, String status) {
        this.productId = productId;
        this.barcode = barcode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brand = brand;
        this.targetGroup = targetGroup;
        this.size = size;
        this.color = color;
        this.sellingPrice = sellingPrice;
        this.discountPercentage = discountPercentage;
        this.stockQuantity = stockQuantity;
        this.status = status;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTargetGroup() {
        return targetGroup != null ? targetGroup : "";
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public String getSize() {
        return size != null ? size : "";
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color != null ? color : "";
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Calculated Helper: Returns the final discounted price
     */
    public double getDiscountedPrice() {
        return sellingPrice - (sellingPrice * (discountPercentage / 100.0));
    }

    @Override
    public String toString() {
        return productName + " (" + barcode + ")";
    }
}
