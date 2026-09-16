/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.models;

import fashionflairboutique.views.POSFrame;

/**
 *
 * @author Chethiya
 */
public class POS {
    private int productId;      // FIX: added - required to insert into Sale_Items correctly
    private String barcode;
    private String productName;
    private double price;       // maps to products.selling_price
    private int availableQty;   // maps to products.stock_quantity
    private int qty;            // quantity being purchased on this line
    private String paymentMethod;
    private double discount;    // product's standing discount_percentage (see discount rule below)
    private double total;
    private double paid;
    private double change;

    public POS() {
    }

    // Parameterized Constructor (kept for backward compatibility)
    public POS(String barcode, String productName, double price, int availableQty,
                    int qty, String paymentMethod, double discount, double total,
                    double paid, double change) {
        this.barcode = barcode;
        this.productName = productName;
        this.price = price;
        this.availableQty = availableQty;
        this.qty = qty;
        this.paymentMethod = paymentMethod;
        this.discount = discount;
        this.total = total;
        this.paid = paid;
        this.change = change;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    /**
     * Convenience helper: unit price after the product's standing discount.
     * Mirrors PCatalog.getDiscountedPrice() - same formula, same reasoning
     * (derived, never stored, so it can never go stale).
     */
    public double getDiscountedPrice() {
        return price - (price * (discount / 100.0));
    }

}
