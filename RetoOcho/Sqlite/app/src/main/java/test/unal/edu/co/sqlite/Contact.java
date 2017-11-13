package test.unal.edu.co.sqlite;

import java.io.Serializable;

public class Contact implements Serializable {
    private String businessName;
    private String category;
    private String website;
    private String phone;
    private String email;
    private String product;
    private String _id;

    public Contact(String businessName, String category, String website, String phone, String email, String product, String _id) {
        this.businessName = businessName;
        this.category = category;
        this.website = website;
        this.phone = phone;
        this.email = email;
        this.product = product;
        this._id = _id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getCategory() {
        return category;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getProduct() {
        return product;
    }

    public String get_id() {
        return _id;
    }
}