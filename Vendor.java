package model;

public class Vendor {

    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;

    // Empty constructor
    public Vendor() {}

    // Constructor
    public Vendor(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // ===== GETTERS =====

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    // ===== SETTERS =====

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}