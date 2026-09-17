/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.models;

/**
 *
 * @author Chethiya
 */
public class SalesReport {
   private int saleId;
    private String invoiceNumber;
    private String cashier;
    private double subtotal;
    private double totalDiscount;
    private double netTotal;
    private String saleDate;

    public SalesReport(int saleId, String invoiceNumber, String cashier, double subtotal, double totalDiscount, double netTotal, String saleDate) {
        this.saleId = saleId;
        this.invoiceNumber = invoiceNumber;
        this.cashier = cashier;
        this.subtotal = subtotal;
        this.totalDiscount = totalDiscount;
        this.netTotal = netTotal;
        this.saleDate = saleDate;
    }

    public int getSaleId() { return saleId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getCashier() { return cashier; }
    public double getSubtotal() { return subtotal; }
    public double getTotalDiscount() { return totalDiscount; }
    public double getNetTotal() { return netTotal; }
    public String getSaleDate() { return saleDate; }
}
