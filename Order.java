package model;

public class Order {

    private int id;
    private int vendorId;
    private String product;
    private int quantity;
    private String status;

    public Order() {}

    public Order(int vendorId, String product, int quantity) {
        this.vendorId = vendorId;
        this.product = product;
        this.quantity = quantity;
        this.status = "Pending";
    }

    // GETTERS

    public int getId() {
        return id;
    }

    public int getVendorId() {
        return vendorId;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    // SETTERS

    public void setId(int id) {
        this.id = id;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}